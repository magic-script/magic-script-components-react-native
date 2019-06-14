import React, { Component } from 'react';

class Demo1App extends Component {

    constructor(props) {
        super(props);
        this.images = [
            require('../res/demo1/slide1.png'),
            require('../res/demo1/slide2.png'),
            require('../res/demo1/slide3.png'),
            require('../res/demo1/slide4.png'),
        ];
    }

    state = { selectedImageIndex: 0 }

    onNextButtonPress = () => {
        const { selectedImageIndex } = this.state;
        if (selectedImageIndex + 1 < this.images.length) {
            this.setState({ selectedImageIndex: selectedImageIndex + 1 });
        }
    }
    
    onPreviousButtonPress = () => {
        const { selectedImageIndex } = this.state;
        if (selectedImageIndex > 0) {
            this.setState({ selectedImageIndex: selectedImageIndex - 1 });
        }
    }
    
    render() {
        const { selectedImageIndex } = this.state;
        const imageSource = this.images[selectedImageIndex];
        return (
            <view name='demo1' position={{ x: 0, y: 0, z: 0 }} scale={0.05}>
                <text position={{ x: 0, y: 3.5, z: 0 }} text={'Demo 1'} textColor={'pink'}></text>
                <image position={{ x: 0, y: 1.5, z: 0 }} source={imageSource} size={{ width: 3, height: 3 }} />
                <view name='control' position={{ x: 0, y: -1, z: 0 }}>
                    <button position={{ x: -1.5, y: 0, z: 0 }} title={'Prev'} color='pink' onPress={this.onPreviousButtonPress} />
                    <button position={{ x: 1.5, y: 0, z: 0 }} title={'Next'} color='pink' onPress={this.onNextButtonPress} />
                </view>
            </view>
        );
    }
}

export default Demo1App;
