import PropTypes from 'prop-types';

const animatableNumber = PropTypes.oneOfType([
  PropTypes.number,
  PropTypes.object,
]);

export const deprecated = (propType, hint = null) => (props, propName, componentName) => {
  if (props[propName]) {
    console.warn(`Prop \`${propName}\` supplied to` + ` \`${componentName}\` is deprecated. ${hint}`);
  }
  return PropTypes.checkPropTypes(
    { [propName]: propType },
    props,
    propName,
    componentName,
  );
};
export const position = PropTypes.shape({
  x: animatableNumber,
  y: animatableNumber,
  z: animatableNumber,
});

export const scale = animatableNumber;
export const categoryBitMask = PropTypes.number;
export const transition = PropTypes.shape({
  duration: PropTypes.number,
});

export const eulerAngles = PropTypes.shape({
  x: animatableNumber,
  y: animatableNumber,
  z: animatableNumber,
});

export const rotation = PropTypes.shape({
  x: animatableNumber,
  y: animatableNumber,
  z: animatableNumber,
  w: animatableNumber,
});

export const orientation = PropTypes.shape({
  x: animatableNumber,
  y: animatableNumber,
  z: animatableNumber,
  w: animatableNumber,
});

export const textureTranslation = PropTypes.shape({
  x: PropTypes.number,
  y: PropTypes.number,
  z: PropTypes.number,
});

export const textureRotation = PropTypes.shape({
  angle: PropTypes.number,
  x: PropTypes.number,
  y: PropTypes.number,
  z: PropTypes.number,
});

export const textureScale = PropTypes.shape({
  x: PropTypes.number,
  y: PropTypes.number,
  z: PropTypes.number,
});

export const castsShadow = PropTypes.bool;
export const renderingOrder = PropTypes.number;
export const color = PropTypes.string;
export const opacity = animatableNumber;

export const materialProperty = PropTypes.shape({
  path: PropTypes.string,
  color: PropTypes.string,
  intensity: PropTypes.number,
  translation: textureTranslation,
  scale: textureScale,
  rotation: textureRotation,
});

export const material = PropTypes.shape({
  color,
  normal: materialProperty,
  specular: materialProperty,
  displacement: materialProperty,
  diffuse: PropTypes.oneOfType([PropTypes.string, materialProperty]),
  metalness: PropTypes.number,
  roughness: PropTypes.number,
  writesToDepthBuffer: PropTypes.bool,
  doubleSided: PropTypes.bool,
  litPerPixel: PropTypes.bool,
  transparency: PropTypes.number,
});

const detectionImage = PropTypes.shape({
  resourceGroupName: PropTypes.string,
});
export const detectionImages = PropTypes.arrayOf(detectionImage);
