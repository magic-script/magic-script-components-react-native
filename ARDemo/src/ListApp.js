import React, { Component } from 'react';

class ListApp extends Component {
    state = { items: [] }

    onAddItemButtonPress = () => {
        var items = this.state.items;
        for (i = 0; i < 5; ++i) {
            const newItem = 'item ' + items.length;
            items.push(newItem);
        }
        this.setState({ items });
    }
    
    onRemoveItemButtonPress = () => {
        var items = this.state.items;
        for (i = 0; i < 5; ++i) {
            items.pop();
        }
        this.setState({ items }); 
    }

    renderList = () => {
        const { items } = this.state;
        return items.map((item, index) => {
            const x = -2 + 2 * Math.floor(index / 5);
            const y = -(index % 5);
            return <text key={index} text={item} position={{ x: x, y, z: 0 }} />;
        });
    }
    
    render() {
        return (
            <view position={{ x: 0, y: 0, z: 0 }}>
                <view name='input' position={{ x: 0, y: 1, z: 0 }}>
                    <button position={{ x: -1.5, y: 0, z: 0 }} title={'Add'} color='cyan' onPress={() => this.onAddItemButtonPress()} />
                    <button position={{ x: 1.5, y: 0, z: 0 }} title={'Remove'} color='cyan' onPress={() => this.onRemoveItemButtonPress()} />
                </view>
                <text text={'text through attribute'}>text through children</text>
                <view name='list'>
                    {this.renderList()}
                </view>
            </view>
        );
    }
}

export default ListApp;