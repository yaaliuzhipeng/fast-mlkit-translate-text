package com.fastmlkittranslatetext;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

class FastMlkitTranslateTextModule extends ReactContextBaseJavaModule {

  private FastMlkitTranslateTextModuleImpl implementation;

  @Override
  @NonNull
  public String getName() {
    return FastMlkitTranslateTextModuleImpl.NAME;
  }

  FastMlkitTranslateTextModule(ReactApplicationContext context) {
    super(context);
    implementation = new FastMlkitTranslateTextModuleImpl();
  }

  @ReactMethod
  public void prepare(ReadableMap options, Promise promise) {
    implementation.prepare(options, promise);
  }

  @ReactMethod
  public void translate(String text, Promise promise) {
    implementation.translate(text, promise);
  }

  @ReactMethod
  public void getDownloadedLanguageModels(Promise promise) {
    implementation.getDownloadedLanguageModels(promise);
  }

  @ReactMethod
  public void deleteLanguageModel(String lang, Promise promise) {
    implementation.deleteLanguageModel(lang, promise);
  }

  @ReactMethod
  public void isLanguageDownloaded(String lang, Promise promise) {
    implementation.isLanguageDownloaded(lang, promise);
  }

  @ReactMethod
  public void downloadLanguageModel(String lang, Promise promise) {
    implementation.downloadLanguageModel(lang, promise);
  }

  @ReactMethod
  public void setIdentifyConfidence(double confidence) {
    implementation.setIdentifyConfidence(confidence);
  }

  @ReactMethod
  public void identify(String text, Promise promise) {
    implementation.identify(text, promise);
  }

  @ReactMethod
  public void identifyPossible(String text, Promise promise) {
    implementation.identifyPossible(text, promise);
  }
}
