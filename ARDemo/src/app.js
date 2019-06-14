import React from 'react';
import { ImageSlider } from './components/index.js';

class MyApp extends React.Component {
  constructor(props) {
    super(props);

    this.images = [
      'resources/DemoPicture1.jpg',
      'resources/DemoPicture2.jpg',
      'resources/DemoPicture4.jpg',
      'resources/DemoPicture5.jpg'
    ];
  }

  render() {
    return (
      <view name='main-view'>
        <ImageSlider
          items={this.images}
          initialPosition={0}
          caption='Gallery'
        ></ImageSlider>
      </view>
    );
  }
}

export default MyApp;
