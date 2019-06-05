import MLButton from './components/MLButton';
import MLImage from './components/MLImage';
import MLText from './components/MLText';
import MLView from './components/MLView';
import { PlatformFactory } from './arkit/platform/platform-factory';

export default {
    button: MLButton,
    image: MLImage,
    text: MLText,
    view: MLView,
    _nativeFactory: new PlatformFactory(),
};
