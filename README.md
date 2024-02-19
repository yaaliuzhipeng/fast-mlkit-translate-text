<p align="center">
  <img src="https://github.com/yaaliuzhipeng/fast-mlkit-translate-text/blob/main/raw/logo.png" alt="MLKitTranslator" width="539" />
</p>

<h4 align="center">
  A MachineLearning Language Translator
</h4>

<p align="center">
  Build Powerful React Native With Greate Language Identifier And Translator <em>Amazing</em> ⚡️
</p>

<p align="center">
  <a href="https://github.com/yaaliuzhipeng/fast-mlkit-translate-text">
    <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="MIT License">
  </a>
</p>

|   | MLKitTranslator |
| - | ------------ |
| ⚡️ | **Identify Language Immediately** over 50 languages supported ([full list](https://developers.google.com/ml-kit/language/translation/translation-language-support)) |
| 😎 | **Lazy loaded**. download models when needed |
| 🔄 | **Offline-first.** no network required for using |
| 📱 | **Multiplatform**. iOS, Android |
| ⏱ | **Fast.** Support New Architecture |
| 🔗 | **Relational.** Built on MLKit [Translation](https://developers.google.com/ml-kit/language/translation) foundation |
| ⚠️ | **Static typing** Full-Support [TypeScript](https://typescriptlang.org) |

## Why MLKitTranslator?

Key capabilities

- Broad language support Translate between more than 50 different languages. See the complete list.
- Proven translation models Powered by the same models used by the Google Translate app's offline mode.
- Dynamic model management Keep on-device storage requirements low by dynamically downloading and managing language packs.
- Runs on the device Translations are performed quickly, and don't require you to send users' text to a remote server.

## Installation

> yarn add fast-mlkit-translate-text

or

> npm i --save fast-mlkit-translate-text


## Usage

**Quick example:** identify language type

```typescript
import FastTranslator from 'fast-mlkit-translate-text';

const text = "你好，朋友";

FastTranslator.identifyLanguage(text)
.then((result:string) => {
	//output: 'en'
})
.catch((err:Error) => {
    //handle error
})

```
**Translate text example:** identify 、download model and translate

etc: you'd like to translate English to Chinese

1️⃣ prepare (when you change the source or target language, you need to call prepare firstly)
```js
import FastTranslator from 'fast-mlkit-translate-text';

await FastTranslator prepare({
    source: 'Chinese';
    target: 'English';
    downloadIfNeeded: true; // set to false if you want to download mannually
});
// await FastTranslator.downloadLanguageModel('Chinese');
// await FastTranslator.downloadLanguageModel('English');
```
**Do not translate text if the model is not downloaded**
you can check model by func **isLanguageDownloaded**
```js
FastTranslator.isLanguageDownloaded('Chinese');
FastTranslator.isLanguageDownloaded('English');
```

2️⃣ translate text
```js
await FastTranslator.translate(text);

// 🎉 got it, dude
```


## Author and license

**FastMLKitTranslateText** was created by [@yaaliuzhipeng](https://github.com/yaaliuzhipeng)

fast-mlkit-translate-text is available under the MIT license. See the [LICENSE file](./LICENSE) for more info.