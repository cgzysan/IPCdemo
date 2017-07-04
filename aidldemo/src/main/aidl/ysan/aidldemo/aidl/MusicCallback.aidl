// MusicCallback.aidl
package ysan.aidldemo.aidl;

// Declare any non-default types here with import statements

interface MusicCallback {
    void onSuccess(String result);
    void onFailure(String error);
}
