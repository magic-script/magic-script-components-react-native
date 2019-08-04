
class Log {
    static info(message, ...optionalParams) {
        this.currentLogger.info(message, ...optionalParams);
    }

    static debug(message, ...optionalParams) {
        this.currentLogger.debug(message, ...optionalParams);
    }

    static warn(message, ...optionalParams) {
        this.currentLogger.warn(message, ...optionalParams);
    }

    static error(message, ...optionalParams) {
        this.currentLogger.error(message, ...optionalParams);
    }

    static setEnabled(enabled) {
        this.currentLogger = enabled ? Log.activeLogger : Log.inactiveLogger;
    }

    static currentLogger = Log.activeLogger;
    static activeLogger = {
        info: (message, ...optionalParams) => { console.info('[MXS] ' + message, ...optionalParams); },
        debug: (message, ...optionalParams) => { console.log('[MXS] ' + message, ...optionalParams); },
        warn: (message, ...optionalParams) => { console.warn('[MXS] ' + message, ...optionalParams); },
        error: (message, ...optionalParams) => { console.error('[MXS] ' + message, ...optionalParams); },
    }
    static inactiveLogger = {
        info: (message, ...optionalParams) => {},
        debug: (message, ...optionalParams) => {},
        warn: (message, ...optionalParams) => {},
        error: (message, ...optionalParams) => {},
    };
}

export { Log };
