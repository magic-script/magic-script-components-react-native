export default class NativeFileSystem {

    constructor() {
        try {
            this.RNFS = require('react-native-fs')
        } catch(e) {
            throw new Error('The RNFS library is not set up correctly. Please visit https://magic-script.org/ for more information.');
        }
    }

    _getFilePath(path) {
        return `${this.RNFS.DocumentDirectoryPath}/${path}`;
    }
    
    writeFile(path, content, encoding) {
        return this.RNFS.writeFile(_getFilePath(path), content, encoding);
    }

    readFile(path, encoding) {
        return this.RNFS.readFile(_getFilePath(path), encoding);
    }

    deleteFile(path) {
        return this.RNFS.unlink(_getFilePath(path));
    }

    moveFile(srcPath, dstPath) {
        return this.RNFS.moveFile(_getFilePath(srcPath), _getFilePath(dstPath));
    }
}