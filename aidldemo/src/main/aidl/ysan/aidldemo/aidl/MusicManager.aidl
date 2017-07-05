// MusicManager.aidl
package ysan.aidldemo.aidl;

// Declare any non-default types here with import statements

import ysan.aidldemo.aidl.MusicSceneInfo;
import ysan.aidldemo.aidl.MusicInfo;
import ysan.aidldemo.aidl.MusicCallback;

interface MusicManager {
    void registerCallback(MusicCallback callback);
    void dealResult(in MusicSceneInfo res);
    void dealOpration(String optator);
}
