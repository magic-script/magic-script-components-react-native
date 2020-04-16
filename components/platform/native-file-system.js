var RNFS = require('react-native-fs');

export default class NativeFileSystem {
    
    writeFile(path, content, encoding) {
        var filePath = RNFS.DocumentDirectoryPath + '/' + path;
        return RNFS.writeFile(filePath, content, encoding)
    }

    readFile(path, encoding) {
        var filePath = RNFS.DocumentDirectoryPath + '/' + path;
        return RNFS.readFile(filePath, encoding)
    }
}