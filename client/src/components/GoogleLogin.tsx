import { useEffect, useRef } from 'react';

interface GoogleLoginProps {
  onSuccess: (idToken: string, clientId: string) => void;
  onError: () => void;
}

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: any) => void;
          renderButton: (element: HTMLElement, config: any) => void;
        };
      };
    };
  }
}

export default function GoogleLogin({ onSuccess, onError }: GoogleLoginProps) {
  const googleLoginRef = useRef<HTMLDivElement>(null);
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

  useEffect(() => {
    if (!clientId) {
      console.error('Google Client ID not configured');
      onError();
      return;
    }

    if (window.google) {
      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: (response: any) => {
          // response.credential là idToken
          onSuccess(response.credential, clientId);
        },
        error_callback: onError,
      });

      if (googleLoginRef.current) {
        window.google.accounts.id.renderButton(googleLoginRef.current, {
          theme: 'outline',
          size: 'large',
        });
      }
    }
  }, [clientId, onSuccess, onError]);

  return <div ref={googleLoginRef}></div>;
}
