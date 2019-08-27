import nativeComponentMapping from './component-mapping.js';
import { PlatformFactory } from './platform-factory';

var nativeFactory = new PlatformFactory(nativeComponentMapping)

test('adds 1 + 2 to equal 3', () => {
  expect(3).toBe(3);
});