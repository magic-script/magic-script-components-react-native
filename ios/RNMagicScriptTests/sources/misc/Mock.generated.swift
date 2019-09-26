// Generated using Sourcery 0.16.1 — https://github.com/krzysztofzablocki/Sourcery
// DO NOT EDIT



// Generated with SwiftyMocky 3.3.4

import SwiftyMocky
#if !MockyCustom
import XCTest
#endif
import UIKit
import SceneKit
import AVKit
@testable import RNMagicScriptHostApplication


// MARK: - AVPlayerProtocol
open class AVPlayerProtocolMock: AVPlayerProtocol, Mock {
    init(sequencing sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst, stubbing stubbingPolicy: StubbingPolicy = .wrap, file: StaticString = #file, line: UInt = #line) {
        SwiftyMockyTestObserver.setup()
        self.sequencingPolicy = sequencingPolicy
        self.stubbingPolicy = stubbingPolicy
        self.file = file
        self.line = line
    }

    var matcher: Matcher = Matcher.default
    var stubbingPolicy: StubbingPolicy = .wrap
    var sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst
    private var invocations: [MethodType] = []
    private var methodReturnValues: [Given] = []
    private var methodPerformValues: [Perform] = []
    private var file: StaticString?
    private var line: UInt?

    public typealias PropertyStub = Given
    public typealias MethodStub = Given
    public typealias SubscriptStub = Given

    /// Convenience method - call setupMock() to extend debug information when failure occurs
    public func setupMock(file: StaticString = #file, line: UInt = #line) {
        self.file = file
        self.line = line
    }

    public var currentItem: AVPlayerItem? {
		get {	invocations.append(.p_currentItem_get); return __p_currentItem ?? optionalGivenGetterValue(.p_currentItem_get, "AVPlayerProtocolMock - stub value for currentItem was not defined") }
		@available(*, deprecated, message: "Using setters on readonly variables is deprecated, and will be removed in 3.1. Use Given to define stubbed property return value.")
		set {	__p_currentItem = newValue }
	}
	private var __p_currentItem: (AVPlayerItem)?

    public var volume: Float {
		get {	invocations.append(.p_volume_get); return __p_volume ?? givenGetterValue(.p_volume_get, "AVPlayerProtocolMock - stub value for volume was not defined") }
		set {	invocations.append(.p_volume_set(.value(newValue))); __p_volume = newValue }
	}
	private var __p_volume: (Float)?





    open func play() {
        addInvocation(.m_play)
		let perform = methodPerformValue(.m_play) as? () -> Void
		perform?()
    }

    open func pause() {
        addInvocation(.m_pause)
		let perform = methodPerformValue(.m_pause) as? () -> Void
		perform?()
    }

    open func seek(to time: CMTime) {
        addInvocation(.m_seek__to_time(Parameter<CMTime>.value(`time`)))
		let perform = methodPerformValue(.m_seek__to_time(Parameter<CMTime>.value(`time`))) as? (CMTime) -> Void
		perform?(`time`)
    }


    fileprivate enum MethodType {
        case m_play
        case m_pause
        case m_seek__to_time(Parameter<CMTime>)
        case p_currentItem_get
        case p_volume_get
		case p_volume_set(Parameter<Float>)

        static func compareParameters(lhs: MethodType, rhs: MethodType, matcher: Matcher) -> Bool {
            switch (lhs, rhs) {
            case (.m_play, .m_play):
                return true 
            case (.m_pause, .m_pause):
                return true 
            case (.m_seek__to_time(let lhsTime), .m_seek__to_time(let rhsTime)):
                guard Parameter.compare(lhs: lhsTime, rhs: rhsTime, with: matcher) else { return false } 
                return true 
            case (.p_currentItem_get,.p_currentItem_get): return true
            case (.p_volume_get,.p_volume_get): return true
			case (.p_volume_set(let left),.p_volume_set(let right)): return Parameter<Float>.compare(lhs: left, rhs: right, with: matcher)
            default: return false
            }
        }

        func intValue() -> Int {
            switch self {
            case .m_play: return 0
            case .m_pause: return 0
            case let .m_seek__to_time(p0): return p0.intValue
            case .p_currentItem_get: return 0
            case .p_volume_get: return 0
			case .p_volume_set(let newValue): return newValue.intValue
            }
        }
    }

    open class Given: StubbedMethod {
        fileprivate var method: MethodType

        private init(method: MethodType, products: [StubProduct]) {
            self.method = method
            super.init(products)
        }

        public static func currentItem(getter defaultValue: AVPlayerItem?...) -> PropertyStub {
            return Given(method: .p_currentItem_get, products: defaultValue.map({ StubProduct.return($0 as Any) }))
        }
        public static func volume(getter defaultValue: Float...) -> PropertyStub {
            return Given(method: .p_volume_get, products: defaultValue.map({ StubProduct.return($0 as Any) }))
        }

    }

    public struct Verify {
        fileprivate var method: MethodType

        public static func play() -> Verify { return Verify(method: .m_play)}
        public static func pause() -> Verify { return Verify(method: .m_pause)}
        public static func seek(to time: Parameter<CMTime>) -> Verify { return Verify(method: .m_seek__to_time(`time`))}
        public static var currentItem: Verify { return Verify(method: .p_currentItem_get) }
        public static var volume: Verify { return Verify(method: .p_volume_get) }
		public static func volume(set newValue: Parameter<Float>) -> Verify { return Verify(method: .p_volume_set(newValue)) }
    }

    public struct Perform {
        fileprivate var method: MethodType
        var performs: Any

        public static func play(perform: @escaping () -> Void) -> Perform {
            return Perform(method: .m_play, performs: perform)
        }
        public static func pause(perform: @escaping () -> Void) -> Perform {
            return Perform(method: .m_pause, performs: perform)
        }
        public static func seek(to time: Parameter<CMTime>, perform: @escaping (CMTime) -> Void) -> Perform {
            return Perform(method: .m_seek__to_time(`time`), performs: perform)
        }
    }

    public func given(_ method: Given) {
        methodReturnValues.append(method)
    }

    public func perform(_ method: Perform) {
        methodPerformValues.append(method)
        methodPerformValues.sort { $0.method.intValue() < $1.method.intValue() }
    }

    public func verify(_ method: Verify, count: Count = Count.moreOrEqual(to: 1), file: StaticString = #file, line: UInt = #line) {
        let invocations = matchingCalls(method.method)
        MockyAssert(count.matches(invocations.count), "Expected: \(count) invocations of `\(method.method)`, but was: \(invocations.count)", file: file, line: line)
    }

    private func addInvocation(_ call: MethodType) {
        invocations.append(call)
    }
    private func methodReturnValue(_ method: MethodType) throws -> StubProduct {
        let candidates = sequencingPolicy.sorted(methodReturnValues, by: { $0.method.intValue() > $1.method.intValue() })
        let matched = candidates.first(where: { $0.isValid && MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) })
        guard let product = matched?.getProduct(policy: self.stubbingPolicy) else { throw MockError.notStubed }
        return product
    }
    private func methodPerformValue(_ method: MethodType) -> Any? {
        let matched = methodPerformValues.reversed().first { MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) }
        return matched?.performs
    }
    private func matchingCalls(_ method: MethodType) -> [MethodType] {
        return invocations.filter { MethodType.compareParameters(lhs: $0, rhs: method, matcher: matcher) }
    }
    private func matchingCalls(_ method: Verify) -> Int {
        return matchingCalls(method.method).count
    }
    private func givenGetterValue<T>(_ method: MethodType, _ message: String) -> T {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            onFatalFailure(message)
            Failure(message)
        }
    }
    private func optionalGivenGetterValue<T>(_ method: MethodType, _ message: String) -> T? {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            return nil
        }
    }
    private func onFatalFailure(_ message: String) {
        #if Mocky
        guard let file = self.file, let line = self.line else { return } // Let if fail if cannot handle gratefully
        SwiftyMockyTestObserver.handleMissingStubError(message: message, file: file, line: line)
        #endif
    }
}

// MARK: - Downloading
open class DownloadingMock: Downloading, Mock {
    init(sequencing sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst, stubbing stubbingPolicy: StubbingPolicy = .wrap, file: StaticString = #file, line: UInt = #line) {
        SwiftyMockyTestObserver.setup()
        self.sequencingPolicy = sequencingPolicy
        self.stubbingPolicy = stubbingPolicy
        self.file = file
        self.line = line
    }

    var matcher: Matcher = Matcher.default
    var stubbingPolicy: StubbingPolicy = .wrap
    var sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst
    private var invocations: [MethodType] = []
    private var methodReturnValues: [Given] = []
    private var methodPerformValues: [Perform] = []
    private var file: StaticString?
    private var line: UInt?

    public typealias PropertyStub = Given
    public typealias MethodStub = Given
    public typealias SubscriptStub = Given

    /// Convenience method - call setupMock() to extend debug information when failure occurs
    public func setupMock(file: StaticString = #file, line: UInt = #line) {
        self.file = file
        self.line = line
    }





    open func downloadModel(modelURL: URL, completion: @escaping (_ localURL: URL?) -> (Void)) {
        addInvocation(.m_downloadModel__modelURL_modelURLcompletion_completion(Parameter<URL>.value(`modelURL`), Parameter<(_ localURL: URL?) -> (Void)>.value(`completion`)))
		let perform = methodPerformValue(.m_downloadModel__modelURL_modelURLcompletion_completion(Parameter<URL>.value(`modelURL`), Parameter<(_ localURL: URL?) -> (Void)>.value(`completion`))) as? (URL, @escaping (_ localURL: URL?) -> (Void)) -> Void
		perform?(`modelURL`, `completion`)
    }


    fileprivate enum MethodType {
        case m_downloadModel__modelURL_modelURLcompletion_completion(Parameter<URL>, Parameter<(_ localURL: URL?) -> (Void)>)

        static func compareParameters(lhs: MethodType, rhs: MethodType, matcher: Matcher) -> Bool {
            switch (lhs, rhs) {
            case (.m_downloadModel__modelURL_modelURLcompletion_completion(let lhsModelurl, let lhsCompletion), .m_downloadModel__modelURL_modelURLcompletion_completion(let rhsModelurl, let rhsCompletion)):
                guard Parameter.compare(lhs: lhsModelurl, rhs: rhsModelurl, with: matcher) else { return false } 
                guard Parameter.compare(lhs: lhsCompletion, rhs: rhsCompletion, with: matcher) else { return false } 
                return true 
            }
        }

        func intValue() -> Int {
            switch self {
            case let .m_downloadModel__modelURL_modelURLcompletion_completion(p0, p1): return p0.intValue + p1.intValue
            }
        }
    }

    open class Given: StubbedMethod {
        fileprivate var method: MethodType

        private init(method: MethodType, products: [StubProduct]) {
            self.method = method
            super.init(products)
        }


    }

    public struct Verify {
        fileprivate var method: MethodType

        public static func downloadModel(modelURL: Parameter<URL>, completion: Parameter<(_ localURL: URL?) -> (Void)>) -> Verify { return Verify(method: .m_downloadModel__modelURL_modelURLcompletion_completion(`modelURL`, `completion`))}
    }

    public struct Perform {
        fileprivate var method: MethodType
        var performs: Any

        public static func downloadModel(modelURL: Parameter<URL>, completion: Parameter<(_ localURL: URL?) -> (Void)>, perform: @escaping (URL, @escaping (_ localURL: URL?) -> (Void)) -> Void) -> Perform {
            return Perform(method: .m_downloadModel__modelURL_modelURLcompletion_completion(`modelURL`, `completion`), performs: perform)
        }
    }

    public func given(_ method: Given) {
        methodReturnValues.append(method)
    }

    public func perform(_ method: Perform) {
        methodPerformValues.append(method)
        methodPerformValues.sort { $0.method.intValue() < $1.method.intValue() }
    }

    public func verify(_ method: Verify, count: Count = Count.moreOrEqual(to: 1), file: StaticString = #file, line: UInt = #line) {
        let invocations = matchingCalls(method.method)
        MockyAssert(count.matches(invocations.count), "Expected: \(count) invocations of `\(method.method)`, but was: \(invocations.count)", file: file, line: line)
    }

    private func addInvocation(_ call: MethodType) {
        invocations.append(call)
    }
    private func methodReturnValue(_ method: MethodType) throws -> StubProduct {
        let candidates = sequencingPolicy.sorted(methodReturnValues, by: { $0.method.intValue() > $1.method.intValue() })
        let matched = candidates.first(where: { $0.isValid && MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) })
        guard let product = matched?.getProduct(policy: self.stubbingPolicy) else { throw MockError.notStubed }
        return product
    }
    private func methodPerformValue(_ method: MethodType) -> Any? {
        let matched = methodPerformValues.reversed().first { MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) }
        return matched?.performs
    }
    private func matchingCalls(_ method: MethodType) -> [MethodType] {
        return invocations.filter { MethodType.compareParameters(lhs: $0, rhs: method, matcher: matcher) }
    }
    private func matchingCalls(_ method: Verify) -> Int {
        return matchingCalls(method.method).count
    }
    private func givenGetterValue<T>(_ method: MethodType, _ message: String) -> T {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            onFatalFailure(message)
            Failure(message)
        }
    }
    private func optionalGivenGetterValue<T>(_ method: MethodType, _ message: String) -> T? {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            return nil
        }
    }
    private func onFatalFailure(_ message: String) {
        #if Mocky
        guard let file = self.file, let line = self.line else { return } // Let if fail if cannot handle gratefully
        SwiftyMockyTestObserver.handleMissingStubError(message: message, file: file, line: line)
        #endif
    }
}

// MARK: - FileManaging
open class FileManagingMock: FileManaging, Mock {
    init(sequencing sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst, stubbing stubbingPolicy: StubbingPolicy = .wrap, file: StaticString = #file, line: UInt = #line) {
        SwiftyMockyTestObserver.setup()
        self.sequencingPolicy = sequencingPolicy
        self.stubbingPolicy = stubbingPolicy
        self.file = file
        self.line = line
    }

    var matcher: Matcher = Matcher.default
    var stubbingPolicy: StubbingPolicy = .wrap
    var sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst
    private var invocations: [MethodType] = []
    private var methodReturnValues: [Given] = []
    private var methodPerformValues: [Perform] = []
    private var file: StaticString?
    private var line: UInt?

    public typealias PropertyStub = Given
    public typealias MethodStub = Given
    public typealias SubscriptStub = Given

    /// Convenience method - call setupMock() to extend debug information when failure occurs
    public func setupMock(file: StaticString = #file, line: UInt = #line) {
        self.file = file
        self.line = line
    }





    open func urls(for directory: FileManager.SearchPathDirectory, in domainMask: FileManager.SearchPathDomainMask) -> [URL] {
        addInvocation(.m_urls__for_directoryin_domainMask(Parameter<FileManager.SearchPathDirectory>.value(`directory`), Parameter<FileManager.SearchPathDomainMask>.value(`domainMask`)))
		let perform = methodPerformValue(.m_urls__for_directoryin_domainMask(Parameter<FileManager.SearchPathDirectory>.value(`directory`), Parameter<FileManager.SearchPathDomainMask>.value(`domainMask`))) as? (FileManager.SearchPathDirectory, FileManager.SearchPathDomainMask) -> Void
		perform?(`directory`, `domainMask`)
		var __value: [URL]
		do {
		    __value = try methodReturnValue(.m_urls__for_directoryin_domainMask(Parameter<FileManager.SearchPathDirectory>.value(`directory`), Parameter<FileManager.SearchPathDomainMask>.value(`domainMask`))).casted()
		} catch {
			onFatalFailure("Stub return value not specified for urls(for directory: FileManager.SearchPathDirectory, in domainMask: FileManager.SearchPathDomainMask). Use given")
			Failure("Stub return value not specified for urls(for directory: FileManager.SearchPathDirectory, in domainMask: FileManager.SearchPathDomainMask). Use given")
		}
		return __value
    }

    open func removeItem(at URL: URL) throws {
        addInvocation(.m_removeItem__at_URL(Parameter<URL>.value(`URL`)))
		let perform = methodPerformValue(.m_removeItem__at_URL(Parameter<URL>.value(`URL`))) as? (URL) -> Void
		perform?(`URL`)
		do {
		    _ = try methodReturnValue(.m_removeItem__at_URL(Parameter<URL>.value(`URL`))).casted() as Void
		} catch MockError.notStubed {
			// do nothing
		} catch {
		    throw error
		}
    }

    open func copyItem(at srcURL: URL, to dstURL: URL) throws {
        addInvocation(.m_copyItem__at_srcURLto_dstURL(Parameter<URL>.value(`srcURL`), Parameter<URL>.value(`dstURL`)))
		let perform = methodPerformValue(.m_copyItem__at_srcURLto_dstURL(Parameter<URL>.value(`srcURL`), Parameter<URL>.value(`dstURL`))) as? (URL, URL) -> Void
		perform?(`srcURL`, `dstURL`)
		do {
		    _ = try methodReturnValue(.m_copyItem__at_srcURLto_dstURL(Parameter<URL>.value(`srcURL`), Parameter<URL>.value(`dstURL`))).casted() as Void
		} catch MockError.notStubed {
			// do nothing
		} catch {
		    throw error
		}
    }


    fileprivate enum MethodType {
        case m_urls__for_directoryin_domainMask(Parameter<FileManager.SearchPathDirectory>, Parameter<FileManager.SearchPathDomainMask>)
        case m_removeItem__at_URL(Parameter<URL>)
        case m_copyItem__at_srcURLto_dstURL(Parameter<URL>, Parameter<URL>)

        static func compareParameters(lhs: MethodType, rhs: MethodType, matcher: Matcher) -> Bool {
            switch (lhs, rhs) {
            case (.m_urls__for_directoryin_domainMask(let lhsDirectory, let lhsDomainmask), .m_urls__for_directoryin_domainMask(let rhsDirectory, let rhsDomainmask)):
                guard Parameter.compare(lhs: lhsDirectory, rhs: rhsDirectory, with: matcher) else { return false } 
                guard Parameter.compare(lhs: lhsDomainmask, rhs: rhsDomainmask, with: matcher) else { return false } 
                return true 
            case (.m_removeItem__at_URL(let lhsUrl), .m_removeItem__at_URL(let rhsUrl)):
                guard Parameter.compare(lhs: lhsUrl, rhs: rhsUrl, with: matcher) else { return false } 
                return true 
            case (.m_copyItem__at_srcURLto_dstURL(let lhsSrcurl, let lhsDsturl), .m_copyItem__at_srcURLto_dstURL(let rhsSrcurl, let rhsDsturl)):
                guard Parameter.compare(lhs: lhsSrcurl, rhs: rhsSrcurl, with: matcher) else { return false } 
                guard Parameter.compare(lhs: lhsDsturl, rhs: rhsDsturl, with: matcher) else { return false } 
                return true 
            default: return false
            }
        }

        func intValue() -> Int {
            switch self {
            case let .m_urls__for_directoryin_domainMask(p0, p1): return p0.intValue + p1.intValue
            case let .m_removeItem__at_URL(p0): return p0.intValue
            case let .m_copyItem__at_srcURLto_dstURL(p0, p1): return p0.intValue + p1.intValue
            }
        }
    }

    open class Given: StubbedMethod {
        fileprivate var method: MethodType

        private init(method: MethodType, products: [StubProduct]) {
            self.method = method
            super.init(products)
        }


        public static func urls(for directory: Parameter<FileManager.SearchPathDirectory>, in domainMask: Parameter<FileManager.SearchPathDomainMask>, willReturn: [URL]...) -> MethodStub {
            return Given(method: .m_urls__for_directoryin_domainMask(`directory`, `domainMask`), products: willReturn.map({ StubProduct.return($0 as Any) }))
        }
        public static func urls(for directory: Parameter<FileManager.SearchPathDirectory>, in domainMask: Parameter<FileManager.SearchPathDomainMask>, willProduce: (Stubber<[URL]>) -> Void) -> MethodStub {
            let willReturn: [[URL]] = []
			let given: Given = { return Given(method: .m_urls__for_directoryin_domainMask(`directory`, `domainMask`), products: willReturn.map({ StubProduct.return($0 as Any) })) }()
			let stubber = given.stub(for: ([URL]).self)
			willProduce(stubber)
			return given
        }
        public static func removeItem(at URL: Parameter<URL>, willThrow: Error...) -> MethodStub {
            return Given(method: .m_removeItem__at_URL(`URL`), products: willThrow.map({ StubProduct.throw($0) }))
        }
        public static func removeItem(at URL: Parameter<URL>, willProduce: (StubberThrows<Void>) -> Void) -> MethodStub {
            let willThrow: [Error] = []
			let given: Given = { return Given(method: .m_removeItem__at_URL(`URL`), products: willThrow.map({ StubProduct.throw($0) })) }()
			let stubber = given.stubThrows(for: (Void).self)
			willProduce(stubber)
			return given
        }
        public static func copyItem(at srcURL: Parameter<URL>, to dstURL: Parameter<URL>, willThrow: Error...) -> MethodStub {
            return Given(method: .m_copyItem__at_srcURLto_dstURL(`srcURL`, `dstURL`), products: willThrow.map({ StubProduct.throw($0) }))
        }
        public static func copyItem(at srcURL: Parameter<URL>, to dstURL: Parameter<URL>, willProduce: (StubberThrows<Void>) -> Void) -> MethodStub {
            let willThrow: [Error] = []
			let given: Given = { return Given(method: .m_copyItem__at_srcURLto_dstURL(`srcURL`, `dstURL`), products: willThrow.map({ StubProduct.throw($0) })) }()
			let stubber = given.stubThrows(for: (Void).self)
			willProduce(stubber)
			return given
        }
    }

    public struct Verify {
        fileprivate var method: MethodType

        public static func urls(for directory: Parameter<FileManager.SearchPathDirectory>, in domainMask: Parameter<FileManager.SearchPathDomainMask>) -> Verify { return Verify(method: .m_urls__for_directoryin_domainMask(`directory`, `domainMask`))}
        public static func removeItem(at URL: Parameter<URL>) -> Verify { return Verify(method: .m_removeItem__at_URL(`URL`))}
        public static func copyItem(at srcURL: Parameter<URL>, to dstURL: Parameter<URL>) -> Verify { return Verify(method: .m_copyItem__at_srcURLto_dstURL(`srcURL`, `dstURL`))}
    }

    public struct Perform {
        fileprivate var method: MethodType
        var performs: Any

        public static func urls(for directory: Parameter<FileManager.SearchPathDirectory>, in domainMask: Parameter<FileManager.SearchPathDomainMask>, perform: @escaping (FileManager.SearchPathDirectory, FileManager.SearchPathDomainMask) -> Void) -> Perform {
            return Perform(method: .m_urls__for_directoryin_domainMask(`directory`, `domainMask`), performs: perform)
        }
        public static func removeItem(at URL: Parameter<URL>, perform: @escaping (URL) -> Void) -> Perform {
            return Perform(method: .m_removeItem__at_URL(`URL`), performs: perform)
        }
        public static func copyItem(at srcURL: Parameter<URL>, to dstURL: Parameter<URL>, perform: @escaping (URL, URL) -> Void) -> Perform {
            return Perform(method: .m_copyItem__at_srcURLto_dstURL(`srcURL`, `dstURL`), performs: perform)
        }
    }

    public func given(_ method: Given) {
        methodReturnValues.append(method)
    }

    public func perform(_ method: Perform) {
        methodPerformValues.append(method)
        methodPerformValues.sort { $0.method.intValue() < $1.method.intValue() }
    }

    public func verify(_ method: Verify, count: Count = Count.moreOrEqual(to: 1), file: StaticString = #file, line: UInt = #line) {
        let invocations = matchingCalls(method.method)
        MockyAssert(count.matches(invocations.count), "Expected: \(count) invocations of `\(method.method)`, but was: \(invocations.count)", file: file, line: line)
    }

    private func addInvocation(_ call: MethodType) {
        invocations.append(call)
    }
    private func methodReturnValue(_ method: MethodType) throws -> StubProduct {
        let candidates = sequencingPolicy.sorted(methodReturnValues, by: { $0.method.intValue() > $1.method.intValue() })
        let matched = candidates.first(where: { $0.isValid && MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) })
        guard let product = matched?.getProduct(policy: self.stubbingPolicy) else { throw MockError.notStubed }
        return product
    }
    private func methodPerformValue(_ method: MethodType) -> Any? {
        let matched = methodPerformValues.reversed().first { MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) }
        return matched?.performs
    }
    private func matchingCalls(_ method: MethodType) -> [MethodType] {
        return invocations.filter { MethodType.compareParameters(lhs: $0, rhs: method, matcher: matcher) }
    }
    private func matchingCalls(_ method: Verify) -> Int {
        return matchingCalls(method.method).count
    }
    private func givenGetterValue<T>(_ method: MethodType, _ message: String) -> T {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            onFatalFailure(message)
            Failure(message)
        }
    }
    private func optionalGivenGetterValue<T>(_ method: MethodType, _ message: String) -> T? {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            return nil
        }
    }
    private func onFatalFailure(_ message: String) {
        #if Mocky
        guard let file = self.file, let line = self.line else { return } // Let if fail if cannot handle gratefully
        SwiftyMockyTestObserver.handleMissingStubError(message: message, file: file, line: line)
        #endif
    }
}

// MARK: - GLTFSceneSourceBuildering
open class GLTFSceneSourceBuilderingMock: GLTFSceneSourceBuildering, Mock {
    init(sequencing sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst, stubbing stubbingPolicy: StubbingPolicy = .wrap, file: StaticString = #file, line: UInt = #line) {
        SwiftyMockyTestObserver.setup()
        self.sequencingPolicy = sequencingPolicy
        self.stubbingPolicy = stubbingPolicy
        self.file = file
        self.line = line
    }

    var matcher: Matcher = Matcher.default
    var stubbingPolicy: StubbingPolicy = .wrap
    var sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst
    private var invocations: [MethodType] = []
    private var methodReturnValues: [Given] = []
    private var methodPerformValues: [Perform] = []
    private var file: StaticString?
    private var line: UInt?

    public typealias PropertyStub = Given
    public typealias MethodStub = Given
    public typealias SubscriptStub = Given

    /// Convenience method - call setupMock() to extend debug information when failure occurs
    public func setupMock(file: StaticString = #file, line: UInt = #line) {
        self.file = file
        self.line = line
    }





    open func build(path: String, options: [SCNSceneSource.LoadingOption : Any]?, extensions: [String:Codable.Type]?) throws -> GLTFSceneSourceProtocol {
        addInvocation(.m_build__path_pathoptions_optionsextensions_extensions(Parameter<String>.value(`path`), Parameter<[SCNSceneSource.LoadingOption : Any]?>.value(`options`), Parameter<[String:Codable.Type]?>.value(`extensions`)))
		let perform = methodPerformValue(.m_build__path_pathoptions_optionsextensions_extensions(Parameter<String>.value(`path`), Parameter<[SCNSceneSource.LoadingOption : Any]?>.value(`options`), Parameter<[String:Codable.Type]?>.value(`extensions`))) as? (String, [SCNSceneSource.LoadingOption : Any]?, [String:Codable.Type]?) -> Void
		perform?(`path`, `options`, `extensions`)
		var __value: GLTFSceneSourceProtocol
		do {
		    __value = try methodReturnValue(.m_build__path_pathoptions_optionsextensions_extensions(Parameter<String>.value(`path`), Parameter<[SCNSceneSource.LoadingOption : Any]?>.value(`options`), Parameter<[String:Codable.Type]?>.value(`extensions`))).casted()
		} catch MockError.notStubed {
			onFatalFailure("Stub return value not specified for build(path: String, options: [SCNSceneSource.LoadingOption : Any]?, extensions: [String:Codable.Type]?). Use given")
			Failure("Stub return value not specified for build(path: String, options: [SCNSceneSource.LoadingOption : Any]?, extensions: [String:Codable.Type]?). Use given")
		} catch {
		    throw error
		}
		return __value
    }


    fileprivate enum MethodType {
        case m_build__path_pathoptions_optionsextensions_extensions(Parameter<String>, Parameter<[SCNSceneSource.LoadingOption : Any]?>, Parameter<[String:Codable.Type]?>)

        static func compareParameters(lhs: MethodType, rhs: MethodType, matcher: Matcher) -> Bool {
            switch (lhs, rhs) {
            case (.m_build__path_pathoptions_optionsextensions_extensions(let lhsPath, let lhsOptions, let lhsExtensions), .m_build__path_pathoptions_optionsextensions_extensions(let rhsPath, let rhsOptions, let rhsExtensions)):
                guard Parameter.compare(lhs: lhsPath, rhs: rhsPath, with: matcher) else { return false } 
                guard Parameter.compare(lhs: lhsOptions, rhs: rhsOptions, with: matcher) else { return false } 
                guard Parameter.compare(lhs: lhsExtensions, rhs: rhsExtensions, with: matcher) else { return false } 
                return true 
            }
        }

        func intValue() -> Int {
            switch self {
            case let .m_build__path_pathoptions_optionsextensions_extensions(p0, p1, p2): return p0.intValue + p1.intValue + p2.intValue
            }
        }
    }

    open class Given: StubbedMethod {
        fileprivate var method: MethodType

        private init(method: MethodType, products: [StubProduct]) {
            self.method = method
            super.init(products)
        }


        public static func build(path: Parameter<String>, options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, extensions: Parameter<[String:Codable.Type]?>, willReturn: GLTFSceneSourceProtocol...) -> MethodStub {
            return Given(method: .m_build__path_pathoptions_optionsextensions_extensions(`path`, `options`, `extensions`), products: willReturn.map({ StubProduct.return($0 as Any) }))
        }
        public static func build(path: Parameter<String>, options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, extensions: Parameter<[String:Codable.Type]?>, willThrow: Error...) -> MethodStub {
            return Given(method: .m_build__path_pathoptions_optionsextensions_extensions(`path`, `options`, `extensions`), products: willThrow.map({ StubProduct.throw($0) }))
        }
        public static func build(path: Parameter<String>, options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, extensions: Parameter<[String:Codable.Type]?>, willProduce: (StubberThrows<GLTFSceneSourceProtocol>) -> Void) -> MethodStub {
            let willThrow: [Error] = []
			let given: Given = { return Given(method: .m_build__path_pathoptions_optionsextensions_extensions(`path`, `options`, `extensions`), products: willThrow.map({ StubProduct.throw($0) })) }()
			let stubber = given.stubThrows(for: (GLTFSceneSourceProtocol).self)
			willProduce(stubber)
			return given
        }
    }

    public struct Verify {
        fileprivate var method: MethodType

        public static func build(path: Parameter<String>, options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, extensions: Parameter<[String:Codable.Type]?>) -> Verify { return Verify(method: .m_build__path_pathoptions_optionsextensions_extensions(`path`, `options`, `extensions`))}
    }

    public struct Perform {
        fileprivate var method: MethodType
        var performs: Any

        public static func build(path: Parameter<String>, options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, extensions: Parameter<[String:Codable.Type]?>, perform: @escaping (String, [SCNSceneSource.LoadingOption : Any]?, [String:Codable.Type]?) -> Void) -> Perform {
            return Perform(method: .m_build__path_pathoptions_optionsextensions_extensions(`path`, `options`, `extensions`), performs: perform)
        }
    }

    public func given(_ method: Given) {
        methodReturnValues.append(method)
    }

    public func perform(_ method: Perform) {
        methodPerformValues.append(method)
        methodPerformValues.sort { $0.method.intValue() < $1.method.intValue() }
    }

    public func verify(_ method: Verify, count: Count = Count.moreOrEqual(to: 1), file: StaticString = #file, line: UInt = #line) {
        let invocations = matchingCalls(method.method)
        MockyAssert(count.matches(invocations.count), "Expected: \(count) invocations of `\(method.method)`, but was: \(invocations.count)", file: file, line: line)
    }

    private func addInvocation(_ call: MethodType) {
        invocations.append(call)
    }
    private func methodReturnValue(_ method: MethodType) throws -> StubProduct {
        let candidates = sequencingPolicy.sorted(methodReturnValues, by: { $0.method.intValue() > $1.method.intValue() })
        let matched = candidates.first(where: { $0.isValid && MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) })
        guard let product = matched?.getProduct(policy: self.stubbingPolicy) else { throw MockError.notStubed }
        return product
    }
    private func methodPerformValue(_ method: MethodType) -> Any? {
        let matched = methodPerformValues.reversed().first { MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) }
        return matched?.performs
    }
    private func matchingCalls(_ method: MethodType) -> [MethodType] {
        return invocations.filter { MethodType.compareParameters(lhs: $0, rhs: method, matcher: matcher) }
    }
    private func matchingCalls(_ method: Verify) -> Int {
        return matchingCalls(method.method).count
    }
    private func givenGetterValue<T>(_ method: MethodType, _ message: String) -> T {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            onFatalFailure(message)
            Failure(message)
        }
    }
    private func optionalGivenGetterValue<T>(_ method: MethodType, _ message: String) -> T? {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            return nil
        }
    }
    private func onFatalFailure(_ message: String) {
        #if Mocky
        guard let file = self.file, let line = self.line else { return } // Let if fail if cannot handle gratefully
        SwiftyMockyTestObserver.handleMissingStubError(message: message, file: file, line: line)
        #endif
    }
}

// MARK: - GLTFSceneSourceProtocol
open class GLTFSceneSourceProtocolMock: GLTFSceneSourceProtocol, Mock {
    init(sequencing sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst, stubbing stubbingPolicy: StubbingPolicy = .wrap, file: StaticString = #file, line: UInt = #line) {
        SwiftyMockyTestObserver.setup()
        self.sequencingPolicy = sequencingPolicy
        self.stubbingPolicy = stubbingPolicy
        self.file = file
        self.line = line
    }

    var matcher: Matcher = Matcher.default
    var stubbingPolicy: StubbingPolicy = .wrap
    var sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst
    private var invocations: [MethodType] = []
    private var methodReturnValues: [Given] = []
    private var methodPerformValues: [Perform] = []
    private var file: StaticString?
    private var line: UInt?

    public typealias PropertyStub = Given
    public typealias MethodStub = Given
    public typealias SubscriptStub = Given

    /// Convenience method - call setupMock() to extend debug information when failure occurs
    public func setupMock(file: StaticString = #file, line: UInt = #line) {
        self.file = file
        self.line = line
    }





    open func scene(options: [SCNSceneSource.LoadingOption : Any]?) throws -> SCNScene {
        addInvocation(.m_scene__options_options(Parameter<[SCNSceneSource.LoadingOption : Any]?>.value(`options`)))
		let perform = methodPerformValue(.m_scene__options_options(Parameter<[SCNSceneSource.LoadingOption : Any]?>.value(`options`))) as? ([SCNSceneSource.LoadingOption : Any]?) -> Void
		perform?(`options`)
		var __value: SCNScene
		do {
		    __value = try methodReturnValue(.m_scene__options_options(Parameter<[SCNSceneSource.LoadingOption : Any]?>.value(`options`))).casted()
		} catch MockError.notStubed {
			onFatalFailure("Stub return value not specified for scene(options: [SCNSceneSource.LoadingOption : Any]?). Use given")
			Failure("Stub return value not specified for scene(options: [SCNSceneSource.LoadingOption : Any]?). Use given")
		} catch {
		    throw error
		}
		return __value
    }


    fileprivate enum MethodType {
        case m_scene__options_options(Parameter<[SCNSceneSource.LoadingOption : Any]?>)

        static func compareParameters(lhs: MethodType, rhs: MethodType, matcher: Matcher) -> Bool {
            switch (lhs, rhs) {
            case (.m_scene__options_options(let lhsOptions), .m_scene__options_options(let rhsOptions)):
                guard Parameter.compare(lhs: lhsOptions, rhs: rhsOptions, with: matcher) else { return false } 
                return true 
            }
        }

        func intValue() -> Int {
            switch self {
            case let .m_scene__options_options(p0): return p0.intValue
            }
        }
    }

    open class Given: StubbedMethod {
        fileprivate var method: MethodType

        private init(method: MethodType, products: [StubProduct]) {
            self.method = method
            super.init(products)
        }


        public static func scene(options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, willReturn: SCNScene...) -> MethodStub {
            return Given(method: .m_scene__options_options(`options`), products: willReturn.map({ StubProduct.return($0 as Any) }))
        }
        public static func scene(options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, willThrow: Error...) -> MethodStub {
            return Given(method: .m_scene__options_options(`options`), products: willThrow.map({ StubProduct.throw($0) }))
        }
        public static func scene(options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, willProduce: (StubberThrows<SCNScene>) -> Void) -> MethodStub {
            let willThrow: [Error] = []
			let given: Given = { return Given(method: .m_scene__options_options(`options`), products: willThrow.map({ StubProduct.throw($0) })) }()
			let stubber = given.stubThrows(for: (SCNScene).self)
			willProduce(stubber)
			return given
        }
    }

    public struct Verify {
        fileprivate var method: MethodType

        public static func scene(options: Parameter<[SCNSceneSource.LoadingOption : Any]?>) -> Verify { return Verify(method: .m_scene__options_options(`options`))}
    }

    public struct Perform {
        fileprivate var method: MethodType
        var performs: Any

        public static func scene(options: Parameter<[SCNSceneSource.LoadingOption : Any]?>, perform: @escaping ([SCNSceneSource.LoadingOption : Any]?) -> Void) -> Perform {
            return Perform(method: .m_scene__options_options(`options`), performs: perform)
        }
    }

    public func given(_ method: Given) {
        methodReturnValues.append(method)
    }

    public func perform(_ method: Perform) {
        methodPerformValues.append(method)
        methodPerformValues.sort { $0.method.intValue() < $1.method.intValue() }
    }

    public func verify(_ method: Verify, count: Count = Count.moreOrEqual(to: 1), file: StaticString = #file, line: UInt = #line) {
        let invocations = matchingCalls(method.method)
        MockyAssert(count.matches(invocations.count), "Expected: \(count) invocations of `\(method.method)`, but was: \(invocations.count)", file: file, line: line)
    }

    private func addInvocation(_ call: MethodType) {
        invocations.append(call)
    }
    private func methodReturnValue(_ method: MethodType) throws -> StubProduct {
        let candidates = sequencingPolicy.sorted(methodReturnValues, by: { $0.method.intValue() > $1.method.intValue() })
        let matched = candidates.first(where: { $0.isValid && MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) })
        guard let product = matched?.getProduct(policy: self.stubbingPolicy) else { throw MockError.notStubed }
        return product
    }
    private func methodPerformValue(_ method: MethodType) -> Any? {
        let matched = methodPerformValues.reversed().first { MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) }
        return matched?.performs
    }
    private func matchingCalls(_ method: MethodType) -> [MethodType] {
        return invocations.filter { MethodType.compareParameters(lhs: $0, rhs: method, matcher: matcher) }
    }
    private func matchingCalls(_ method: Verify) -> Int {
        return matchingCalls(method.method).count
    }
    private func givenGetterValue<T>(_ method: MethodType, _ message: String) -> T {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            onFatalFailure(message)
            Failure(message)
        }
    }
    private func optionalGivenGetterValue<T>(_ method: MethodType, _ message: String) -> T? {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            return nil
        }
    }
    private func onFatalFailure(_ message: String) {
        #if Mocky
        guard let file = self.file, let line = self.line else { return } // Let if fail if cannot handle gratefully
        SwiftyMockyTestObserver.handleMissingStubError(message: message, file: file, line: line)
        #endif
    }
}

// MARK: - URLSessionDownloadingTask
open class URLSessionDownloadingTaskMock: URLSessionDownloadingTask, Mock {
    init(sequencing sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst, stubbing stubbingPolicy: StubbingPolicy = .wrap, file: StaticString = #file, line: UInt = #line) {
        SwiftyMockyTestObserver.setup()
        self.sequencingPolicy = sequencingPolicy
        self.stubbingPolicy = stubbingPolicy
        self.file = file
        self.line = line
    }

    var matcher: Matcher = Matcher.default
    var stubbingPolicy: StubbingPolicy = .wrap
    var sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst
    private var invocations: [MethodType] = []
    private var methodReturnValues: [Given] = []
    private var methodPerformValues: [Perform] = []
    private var file: StaticString?
    private var line: UInt?

    public typealias PropertyStub = Given
    public typealias MethodStub = Given
    public typealias SubscriptStub = Given

    /// Convenience method - call setupMock() to extend debug information when failure occurs
    public func setupMock(file: StaticString = #file, line: UInt = #line) {
        self.file = file
        self.line = line
    }





    open func resume() {
        addInvocation(.m_resume)
		let perform = methodPerformValue(.m_resume) as? () -> Void
		perform?()
    }

    open func cancel() {
        addInvocation(.m_cancel)
		let perform = methodPerformValue(.m_cancel) as? () -> Void
		perform?()
    }


    fileprivate enum MethodType {
        case m_resume
        case m_cancel

        static func compareParameters(lhs: MethodType, rhs: MethodType, matcher: Matcher) -> Bool {
            switch (lhs, rhs) {
            case (.m_resume, .m_resume):
                return true 
            case (.m_cancel, .m_cancel):
                return true 
            default: return false
            }
        }

        func intValue() -> Int {
            switch self {
            case .m_resume: return 0
            case .m_cancel: return 0
            }
        }
    }

    open class Given: StubbedMethod {
        fileprivate var method: MethodType

        private init(method: MethodType, products: [StubProduct]) {
            self.method = method
            super.init(products)
        }


    }

    public struct Verify {
        fileprivate var method: MethodType

        public static func resume() -> Verify { return Verify(method: .m_resume)}
        public static func cancel() -> Verify { return Verify(method: .m_cancel)}
    }

    public struct Perform {
        fileprivate var method: MethodType
        var performs: Any

        public static func resume(perform: @escaping () -> Void) -> Perform {
            return Perform(method: .m_resume, performs: perform)
        }
        public static func cancel(perform: @escaping () -> Void) -> Perform {
            return Perform(method: .m_cancel, performs: perform)
        }
    }

    public func given(_ method: Given) {
        methodReturnValues.append(method)
    }

    public func perform(_ method: Perform) {
        methodPerformValues.append(method)
        methodPerformValues.sort { $0.method.intValue() < $1.method.intValue() }
    }

    public func verify(_ method: Verify, count: Count = Count.moreOrEqual(to: 1), file: StaticString = #file, line: UInt = #line) {
        let invocations = matchingCalls(method.method)
        MockyAssert(count.matches(invocations.count), "Expected: \(count) invocations of `\(method.method)`, but was: \(invocations.count)", file: file, line: line)
    }

    private func addInvocation(_ call: MethodType) {
        invocations.append(call)
    }
    private func methodReturnValue(_ method: MethodType) throws -> StubProduct {
        let candidates = sequencingPolicy.sorted(methodReturnValues, by: { $0.method.intValue() > $1.method.intValue() })
        let matched = candidates.first(where: { $0.isValid && MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) })
        guard let product = matched?.getProduct(policy: self.stubbingPolicy) else { throw MockError.notStubed }
        return product
    }
    private func methodPerformValue(_ method: MethodType) -> Any? {
        let matched = methodPerformValues.reversed().first { MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) }
        return matched?.performs
    }
    private func matchingCalls(_ method: MethodType) -> [MethodType] {
        return invocations.filter { MethodType.compareParameters(lhs: $0, rhs: method, matcher: matcher) }
    }
    private func matchingCalls(_ method: Verify) -> Int {
        return matchingCalls(method.method).count
    }
    private func givenGetterValue<T>(_ method: MethodType, _ message: String) -> T {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            onFatalFailure(message)
            Failure(message)
        }
    }
    private func optionalGivenGetterValue<T>(_ method: MethodType, _ message: String) -> T? {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            return nil
        }
    }
    private func onFatalFailure(_ message: String) {
        #if Mocky
        guard let file = self.file, let line = self.line else { return } // Let if fail if cannot handle gratefully
        SwiftyMockyTestObserver.handleMissingStubError(message: message, file: file, line: line)
        #endif
    }
}

// MARK: - URLSessioning
open class URLSessioningMock: URLSessioning, Mock {
    init(sequencing sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst, stubbing stubbingPolicy: StubbingPolicy = .wrap, file: StaticString = #file, line: UInt = #line) {
        SwiftyMockyTestObserver.setup()
        self.sequencingPolicy = sequencingPolicy
        self.stubbingPolicy = stubbingPolicy
        self.file = file
        self.line = line
    }

    var matcher: Matcher = Matcher.default
    var stubbingPolicy: StubbingPolicy = .wrap
    var sequencingPolicy: SequencingPolicy = .lastWrittenResolvedFirst
    private var invocations: [MethodType] = []
    private var methodReturnValues: [Given] = []
    private var methodPerformValues: [Perform] = []
    private var file: StaticString?
    private var line: UInt?

    public typealias PropertyStub = Given
    public typealias MethodStub = Given
    public typealias SubscriptStub = Given

    /// Convenience method - call setupMock() to extend debug information when failure occurs
    public func setupMock(file: StaticString = #file, line: UInt = #line) {
        self.file = file
        self.line = line
    }





    open func downloadTask_(with url: URL, completionHandler: @escaping (URL?, URLResponse?, Error?) -> Void) -> URLSessionDownloadingTask {
        addInvocation(.m_downloadTask__with_urlcompletionHandler_completionHandler(Parameter<URL>.value(`url`), Parameter<(URL?, URLResponse?, Error?) -> Void>.value(`completionHandler`)))
		let perform = methodPerformValue(.m_downloadTask__with_urlcompletionHandler_completionHandler(Parameter<URL>.value(`url`), Parameter<(URL?, URLResponse?, Error?) -> Void>.value(`completionHandler`))) as? (URL, @escaping (URL?, URLResponse?, Error?) -> Void) -> Void
		perform?(`url`, `completionHandler`)
		var __value: URLSessionDownloadingTask
		do {
		    __value = try methodReturnValue(.m_downloadTask__with_urlcompletionHandler_completionHandler(Parameter<URL>.value(`url`), Parameter<(URL?, URLResponse?, Error?) -> Void>.value(`completionHandler`))).casted()
		} catch {
			onFatalFailure("Stub return value not specified for downloadTask_(with url: URL, completionHandler: @escaping (URL?, URLResponse?, Error?) -> Void). Use given")
			Failure("Stub return value not specified for downloadTask_(with url: URL, completionHandler: @escaping (URL?, URLResponse?, Error?) -> Void). Use given")
		}
		return __value
    }


    fileprivate enum MethodType {
        case m_downloadTask__with_urlcompletionHandler_completionHandler(Parameter<URL>, Parameter<(URL?, URLResponse?, Error?) -> Void>)

        static func compareParameters(lhs: MethodType, rhs: MethodType, matcher: Matcher) -> Bool {
            switch (lhs, rhs) {
            case (.m_downloadTask__with_urlcompletionHandler_completionHandler(let lhsUrl, let lhsCompletionhandler), .m_downloadTask__with_urlcompletionHandler_completionHandler(let rhsUrl, let rhsCompletionhandler)):
                guard Parameter.compare(lhs: lhsUrl, rhs: rhsUrl, with: matcher) else { return false } 
                guard Parameter.compare(lhs: lhsCompletionhandler, rhs: rhsCompletionhandler, with: matcher) else { return false } 
                return true 
            }
        }

        func intValue() -> Int {
            switch self {
            case let .m_downloadTask__with_urlcompletionHandler_completionHandler(p0, p1): return p0.intValue + p1.intValue
            }
        }
    }

    open class Given: StubbedMethod {
        fileprivate var method: MethodType

        private init(method: MethodType, products: [StubProduct]) {
            self.method = method
            super.init(products)
        }


        public static func downloadTask_(with url: Parameter<URL>, completionHandler: Parameter<(URL?, URLResponse?, Error?) -> Void>, willReturn: URLSessionDownloadingTask...) -> MethodStub {
            return Given(method: .m_downloadTask__with_urlcompletionHandler_completionHandler(`url`, `completionHandler`), products: willReturn.map({ StubProduct.return($0 as Any) }))
        }
        public static func downloadTask_(with url: Parameter<URL>, completionHandler: Parameter<(URL?, URLResponse?, Error?) -> Void>, willProduce: (Stubber<URLSessionDownloadingTask>) -> Void) -> MethodStub {
            let willReturn: [URLSessionDownloadingTask] = []
			let given: Given = { return Given(method: .m_downloadTask__with_urlcompletionHandler_completionHandler(`url`, `completionHandler`), products: willReturn.map({ StubProduct.return($0 as Any) })) }()
			let stubber = given.stub(for: (URLSessionDownloadingTask).self)
			willProduce(stubber)
			return given
        }
    }

    public struct Verify {
        fileprivate var method: MethodType

        public static func downloadTask_(with url: Parameter<URL>, completionHandler: Parameter<(URL?, URLResponse?, Error?) -> Void>) -> Verify { return Verify(method: .m_downloadTask__with_urlcompletionHandler_completionHandler(`url`, `completionHandler`))}
    }

    public struct Perform {
        fileprivate var method: MethodType
        var performs: Any

        public static func downloadTask_(with url: Parameter<URL>, completionHandler: Parameter<(URL?, URLResponse?, Error?) -> Void>, perform: @escaping (URL, @escaping (URL?, URLResponse?, Error?) -> Void) -> Void) -> Perform {
            return Perform(method: .m_downloadTask__with_urlcompletionHandler_completionHandler(`url`, `completionHandler`), performs: perform)
        }
    }

    public func given(_ method: Given) {
        methodReturnValues.append(method)
    }

    public func perform(_ method: Perform) {
        methodPerformValues.append(method)
        methodPerformValues.sort { $0.method.intValue() < $1.method.intValue() }
    }

    public func verify(_ method: Verify, count: Count = Count.moreOrEqual(to: 1), file: StaticString = #file, line: UInt = #line) {
        let invocations = matchingCalls(method.method)
        MockyAssert(count.matches(invocations.count), "Expected: \(count) invocations of `\(method.method)`, but was: \(invocations.count)", file: file, line: line)
    }

    private func addInvocation(_ call: MethodType) {
        invocations.append(call)
    }
    private func methodReturnValue(_ method: MethodType) throws -> StubProduct {
        let candidates = sequencingPolicy.sorted(methodReturnValues, by: { $0.method.intValue() > $1.method.intValue() })
        let matched = candidates.first(where: { $0.isValid && MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) })
        guard let product = matched?.getProduct(policy: self.stubbingPolicy) else { throw MockError.notStubed }
        return product
    }
    private func methodPerformValue(_ method: MethodType) -> Any? {
        let matched = methodPerformValues.reversed().first { MethodType.compareParameters(lhs: $0.method, rhs: method, matcher: matcher) }
        return matched?.performs
    }
    private func matchingCalls(_ method: MethodType) -> [MethodType] {
        return invocations.filter { MethodType.compareParameters(lhs: $0, rhs: method, matcher: matcher) }
    }
    private func matchingCalls(_ method: Verify) -> Int {
        return matchingCalls(method.method).count
    }
    private func givenGetterValue<T>(_ method: MethodType, _ message: String) -> T {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            onFatalFailure(message)
            Failure(message)
        }
    }
    private func optionalGivenGetterValue<T>(_ method: MethodType, _ message: String) -> T? {
        do {
            return try methodReturnValue(method).casted()
        } catch {
            return nil
        }
    }
    private func onFatalFailure(_ message: String) {
        #if Mocky
        guard let file = self.file, let line = self.line else { return } // Let if fail if cannot handle gratefully
        SwiftyMockyTestObserver.handleMissingStubError(message: message, file: file, line: line)
        #endif
    }
}
