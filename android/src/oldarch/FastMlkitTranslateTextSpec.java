package com.fastmlkittranslatetext;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;

abstract class FastMlkitTranslateTextSpec extends ReactContextBaseJavaModule {
  FastMlkitTranslateTextSpec(ReactApplicationContext context) {
    super(context);
  }

  public abstract void multiply(double a, double b, Promise promise);
}
