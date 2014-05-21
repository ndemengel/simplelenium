/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.simplelenium;

import static java.lang.String.*;
import static java.util.stream.Stream.*;
import static net.codestory.simplelenium.Verification.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import org.openqa.selenium.*;

public class Should {
  private final WebDriver driver;
  private final By selector;
  private final Retry retry;
  private final boolean not;

  Should(WebDriver driver, By selector, long duration, TimeUnit timeUnit) {
    this(driver, selector, new Retry(duration, timeUnit), false);
  }

  private Should(WebDriver driver, By selector, Retry retry, boolean not) {
    this.driver = driver;
    this.selector = selector;
    this.retry = retry;
    this.not = not;
  }

  public Should not() {
    return new Should(driver, selector, retry, !not);
  }

  public void contain(String... texts) {
    verify(
        "contains(" + join(";", texts) + ")",
        this::find,
        element -> of(texts).allMatch(expected -> element.getText().contains(expected)),
        element -> "hasText(" + element.getText() + ")");
  }

  public void notContain(String text) {
    verify("does not contain (" + text + ")", this::find, element -> !element.getText().contains(text));
  }

  public void beEnabled() {
    verify("is enabled", this::find, element -> element.isEnabled());
  }

  public void beDisabled() {
    verify("is disabled", this::find, element -> !element.isEnabled());
  }

  public void beDisplayed() {
    verify("is displayed", this::find, element -> element.isDisplayed());
  }

  public void beHidden() {
    verify("is hidden", this::find, element -> !element.isDisplayed());
  }

  public void beSelected() {
    verify("is selected", this::find, element -> element.isSelected());
  }

  public void haveNoMoreItemsThan(int maxCount) {
    verify("has at most " + maxCount + " items", this::findMultiple, elements -> elements.size() <= maxCount);
  }

  public void haveSize(int size) {
    verify("has size " + size, this::findMultiple, elements -> elements.size() == size);
  }

  public void beEmpty() {
    verify("is empty", this::findMultiple, elements -> elements.isEmpty());
  }

  private <T> void verify(String message, Supplier<T> target, Predicate<T> predicate) {
    verify(message, target, predicate, null);
  }

  private <T> void verify(String verificationDesc, Supplier<T> target, Predicate<T> predicate, Function<T, String> clueExtractor) {
    String selectorDesc = toString(selector);

    System.out.println("   -> verify that " + selectorDesc + " " + verificationDesc);

    Verification result = retry.verify(target, predicate, clueExtractor);
    if (!result.isNotFound() && result.isOk() != not) {
      return; // success
    }

    throw new AssertionError(result.description(selectorDesc, verificationDesc, not));
  }

  private static String toString(By selector) {
    return selector.toString().replace("By.selector: ", "");
  }

  private WebElement find() {
    return driver.findElement(selector);
  }

  private List<WebElement> findMultiple() {
    return driver.findElements(selector);
  }
}
