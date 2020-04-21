var RNFS = require('react-native-fs');

export default class NativeFileSystem {
    
    writeFile(path, content, encoding) {
        var filePath = RNFS.DocumentDirectoryPath + '/' + path;
        return RNFS.writeFile(filePath, content, encoding);
    }

    readFile(path, encoding) {
        var filePath = RNFS.DocumentDirectoryPath + '/' + path;
        return RNFS.readFile(filePath, encoding);
    }

    deleteFile(path) {
        var filePath = RNFS.DocumentDirectoryPath + '/' + path;
        return RNFS.unlink(filePath);
    }

    moveFile(srcPath, dstPath) {
        var sourcePath = RNFS.DocumentDirectoryPath + '/' + srcPath;
        var destPath = RNFS.DocumentDirectoryPath + '/' + dstPath;
        return RNFS.moveFile(sourcePath, destPath);
    }
}