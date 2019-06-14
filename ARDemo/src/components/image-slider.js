import React from 'react';

export default class ImageSlider extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            items: props.items,
            currentPosition: props.initialPosition
        };

        this.onPreviousClick = this.onPreviousClick.bind(this);
        this.onNextClick = this.onNextClick.bind(this);
    }

    onPreviousClick(event) {
        const position = this.state.currentPosition === 0
            ? this.state.items.length - 1
            : this.state.currentPosition - 1;

        this.setState({ currentPosition:  position });
    }

    onNextClick(event) {
        const position = this.state.currentPosition + 1 === this.state.items.length
            ? 0
            : this.state.currentPosition + 1;

        this.setState({ currentPosition: position });
    }

    render() {
        return (
            <view>
                <text
                    localPosition={[-0.07, 0.30, 0]}
                    textSize={0.05}
                >{this.props.caption}</text>

                <image
                    key={this.state.currentPosition}
                    filePath={this.state.items[this.state.currentPosition]}
                    // localPosition={[-0.25,  0.25,  -0.25]}
                    width={0.75}
                    height={0.5}
                ></image>

                <button
                    localPosition={[-0.25, -0.35, 0]}
                    width={0.25}
                    height={0.10}
                    roundness={0.5}
                    textSize={0.035}
                    onClick={this.onPreviousClick}
                >previous</button>
                <button
                    localPosition={[ 0.25, -0.35, 0]}
                    width={0.25}
                    height={0.10}
                    roundness={0.5}
                    textSize={0.035}
                    onClick={this.onNextClick}
                >next</button>
            </view>
        );
    }
}
