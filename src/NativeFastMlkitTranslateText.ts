import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  prepare(options: {
    source: string;
    target: string;
    downloadIfNeeded?: boolean;
  }): Promise<boolean>;
  translate(text: string): Promise<string>;
  getDownloadedLanguageModels(): Promise<Array<string>>;
  deleteLanguageModel(lang: string): Promise<boolean>;
  downloadLanguageModel(lang: string): Promise<boolean>;
  isLanguageDownloaded(lang: string): Promise<boolean>;
  setIdentifyConfidence(confidence: number): void;
  identify(text: string): Promise<string>;
  identifyPossible(text: string): Promise<Array<{ lang: string; confidence: number }>>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('FastMlkitTranslateText');
