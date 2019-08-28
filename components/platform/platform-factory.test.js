import nativeComponentMapping from './component-mapping.js';
import { PlatformFactory } from './platform-factory';

var platformFactory = new PlatformFactory(nativeComponentMapping)

test('name of created element must be string', () => {
  expect(() => {
    platformFactory.createElement(1, 2, 3)
  }).toThrowError('string');
});

test('only known elements can be created', () => {
  expect(() => {
    platformFactory.createElement("unknown element", 2, 3)
  }).toThrowError('Unknown');
});