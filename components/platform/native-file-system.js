var RNFS = require('react-native-fs');

export default class NativeFileSystem {
    
    writeFile(path, content, encoding) {
        return RNFS.writeFile(path, content, encoding)
    }

    readFile(path, encoding) {
        return RNFS.readFile(path, encoding)
    }
}