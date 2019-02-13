using UnityEngine;
using System.Collections;

public class MoverioCardboard : MonoBehaviour {
	
	
	private AndroidJavaClass _unityPlayer;
	private AndroidJavaObject _currentActivity;
	private AndroidJavaObject _BT200Plugin = null;
	//private bool MoverioDevice = true;
	
	// The singleton instance of the MoverioCardboard class.
	private static MoverioCardboard _instance = null;
	
	public static MoverioCardboard SDK {
		get {
			if (_instance == null) {
				Debug.Log("Please add Moverio Cardboard to scene");
			}
			return _instance;
		}
	}
	
	void Awake () {
		_instance = this;
	}
	
	void Start () {
		SetJavaClass();
	}
	
	void Update () {
		if (Input.GetKeyUp(KeyCode.Menu)) {
		
			Debug.Log("C# menu key pusshed:");
			Cardboard.SDK.ToggleVRMode();
		}	
		if (Input.GetMouseButtonDown (0)) {
			Debug.Log("C# mouse tap:");
			//Cardboard.SDK.ToggleVRMode();
		}
		
		
	}
	

	
	void SetJavaClass() {
		
		//if(SystemInfo.deviceModel.Equals("EPSON embt2")){
			AndroidJNI.AttachCurrentThread();
		//} else {
			//MoverioDevice = false;
		//}	
		
		Debug.Log("Start:SetJavaClass");
		
		#if UNITY_ANDROID && !UNITY_EDITOR
		
		Debug.Log("getting unity activity..");
			using(_unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
			{
				_currentActivity = _unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
			}
			
		Debug.Log(".. got unity activity!");
		Debug.Log("getting plugin activity...");
			using(AndroidJavaClass pluginClass = new AndroidJavaClass("com.hololeo.moveriocardboard.BT200Plugin")) {
				if(pluginClass != null) {
					_BT200Plugin = pluginClass.CallStatic<AndroidJavaObject>("instance");
				Debug.Log(".. started plugin instance");
					_BT200Plugin.Call("SetContext", _currentActivity);
				Debug.Log(".. set plugin instance");
				}
			}			
		Debug.Log(".. started plugin instance");
			
		#endif
	}
	
	public void SetDisplay3D(bool on) {
		Debug.Log("Moveriocardboard.SDK.SetDisplay3D:" + on);
		#if UNITY_ANDROID && !UNITY_EDITOR
		
		//if(MoverioDevice)
		//{
		Debug.Log("C#:calling setDisplay3D:" + on);
			_BT200Plugin.Call("SetDisplay3D", on);
		//}
		#endif
	}
	
}

