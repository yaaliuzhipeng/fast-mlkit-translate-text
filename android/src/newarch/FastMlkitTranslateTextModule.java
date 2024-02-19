package com.fastmlkittranslatetext;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;


class FastMlkitTranslateTextModule extends NativeFastMlkitTranslateTextSpec {

  private final FastMlkitTranslateTextModuleImpl implementation;

  FastMlkitTranslateTextModule(ReactApplicationContext context) {
    super(context);
    implementation = new FastMlkitTranslateTextModuleImpl();
  }

  @Override
  @NonNull
  public String getName() {
    return FastMlkitTranslateTextModuleImpl.NAME;
  }


  @Override
  @NonNull
  public void prepare(ReadableMap options, Promise promise) {
    implementation.prepare(options, promise);
  }

  @Override
  @NonNull
  public void translate(String text, Promise promise) {
    implementation.translate(text, promise);
  }

  @Override
  @NonNull
  public void getDownloadedLanguageModels(Promise promise) {
    implementation.getDownloadedLanguageModels(promise);
  }

  @Override
  @NonNull
  public void deleteLanguageModel(String lang, Promise promise) {
    implementation.deleteLanguageModel(lang, promise);
  }

  @Override
  @NonNull
  public void isLanguageDownloaded(String lang, Promise promise) {
    implementation.isLanguageDownloaded(lang, promise);
  }

  @Override
  @NonNull
  public void downloadLanguageModel(String lang, Promise promise) {
    implementation.downloadLanguageModel(lang, promise);
  }

  @Override
  @NonNull
  public void setIdentifyConfidence(double confidence) {
    implementation.setIdentifyConfidence(confidence);
  }

  @Override
  @NonNull
  public void identify(String text, Promise promise) {
    implementation.identify(text, promise);
  }

  @Override
  @NonNull
  public void identifyPossible(String text, Promise promise) {
    implementation.identifyPossible(text, promise);
  }

}
