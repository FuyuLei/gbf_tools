
import React, { Component } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
} from 'react-native';

import { GoogleOAuthModule } from './include/NativeModule';

const STATUS = {
  SUCCESS: 'success',
  FAIL: 'fail',
  CANCEL: 'cancel',
};

export default class App extends Component {
  isSignIn = false;
  constructor(props) {
    super(props);

    this.state = {
      account: {},
    };
  }

  componentDidMount = async () => {
    await GoogleOAuthModule.checkUser((status, response) => {
      if (status == STATUS.SUCCESS) {
        this.isSignIn = true;
        this.setState({ account: response });
      } else if (status == STATUS.CANCEL) {
        console.log('leilei checkUser cancel: ', response);
      }
    });
  }

  signIn = async () => {
    await GoogleOAuthModule.signIn((status, response) => {
      if (status == STATUS.SUCCESS) {
        this.isSignIn = true;
        this.setState({ account: response });
      } else if (status == STATUS.FAIL) {
        console.log('leilei signIn fail');
      }
    });
  }

  signOut = async () => {
    await GoogleOAuthModule.signOut((status) => {
      if (status == STATUS.SUCCESS) {
        this.isSignIn = false;
        this.setState({ account: {} });
      }
    });
  }

  render() {
    return (
      <View style={style.cont}>
        {!this.isSignIn ?
          <TouchableOpacity
            style={style.loginCont}
            onPress={() => this.signIn()}>
            <Text style={{ color: 'white', fontSize: 20, fontWeight: '600' }}>Google登入</Text>
          </TouchableOpacity>
          : <View style={{ marginBottom: 50, justifyContent: 'center', alignItems: 'center' }}>
            <View style={{ justifyContent: 'center' }}>
              <Text style={style.accountText}>{`名字： ${this.state.account.name}`}</Text>
              <Text style={style.accountText}>{`信箱： ${this.state.account.email}`}</Text>
            </View><TouchableOpacity
              style={style.loginCont}
              onPress={() => this.signOut()}>
              <Text style={{ color: 'white', fontSize: 20, fontWeight: '600' }}>登出</Text>
            </TouchableOpacity>
          </View>}
      </View>
    );
  }
}

const style = StyleSheet.create({
  cont: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loginCont: {
    height: 40,
    width: 300,
    borderRadius: 5,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#ffc0cb',
  },
  accountText: {
    marginBottom: 10,
    fontSize: 18
  },
});