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
        return this.RNFS.writeFile(this._getFilePath(path), content, encoding);
    }

    readFile(path, encoding) {
        return this.RNFS.readFile(this._getFilePath(path), encoding);
    }

    deleteFile(path) {
        return this.RNFS.unlink(this._getFilePath(path));
    }

    moveFile(srcPath, dstPath) {
        return this.RNFS.moveFile(this._getFilePath(srcPath), this._getFilePath(dstPath));
    }
}