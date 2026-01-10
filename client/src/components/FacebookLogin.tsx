import { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFacebook } from '@fortawesome/free-brands-svg-icons';

interface FacebookLoginProps {
  onSuccess: (accessToken: string, appId: string) => void;
  onError: (error?: any) => void;
}

declare global {
  interface Window {
    FB?: {
      init: (params: {
        appId: string;
        cookie: boolean;
        xfbml: boolean;
        version: string;
      }) => void;
      login: (callback: (response: any) => void, options?: any) => void;
      getLoginStatus: (callback: (response: any) => void) => void;
    };
  }
}

export default function FacebookLogin({ onSuccess, onError }: FacebookLoginProps) {
  const [isSdkLoaded, setIsSdkLoaded] = useState(false);
  const appId = import.meta.env.VITE_FACEBOOK_APP_ID;

  useEffect(() => {
    if (!appId) {
      console.error('Facebook App ID not configured');
      onError('Facebook App ID not configured');
      return;
    }

    const checkSdkLoaded = () => {
      if (window.FB) {
        window.FB.init({
          appId: appId,
          cookie: true,
          xfbml: true,
          version: 'v20.0',
        });
        setIsSdkLoaded(true);
      } else {
        setTimeout(checkSdkLoaded, 100);
      }
    };

    checkSdkLoaded();

  }, [appId, onError]);

  const handleFacebookLogin = () => {
    if (!window.FB || !isSdkLoaded) {
      console.error('Facebook SDK not loaded or initialized');
      onError('Facebook SDK not ready.');
      return;
    }

    window.FB.login(
      (response) => {
        if (response.authResponse) {
          onSuccess(response.authResponse.accessToken, appId);
        } else {
          console.log('User cancelled login or did not fully authorize.');
          onError('User cancelled login.');
        }
      },
      { scope: 'email,public_profile' }
    );
  };

  return (
    <button
      onClick={handleFacebookLogin}
      disabled={!isSdkLoaded}
      className="flex items-center justify-center bg-blue-600 text-white font-sans py-2 px-4 rounded hover:bg-blue-700 transition-colors duration-300 w-full disabled:bg-gray-400 disabled:cursor-not-allowed"
    >
      {isSdkLoaded ? (
        <>
          <FontAwesomeIcon icon={faFacebook} className="mr-2" />
          Sign in with Facebook
        </>
      ) : (
        'Loading Facebook Login...'
      )}
    </button>
  );
}
