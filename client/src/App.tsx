import { useState } from 'react';
import './App.css';
import GoogleLogin from './components/GoogleLogin';
import FacebookLogin from './components/FacebookLogin';

function App() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [user, setUser] = useState<any>(null);

  const handleAuthSuccess = (data: any) => {
    console.log('Login success:', data);
    // Lưu token
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    // Lưu user info
    setUser(data);
  };

  const handleAuthError = (err: any) => {
    setError('Login failed: ' + (err instanceof Error ? err.message : 'Unknown error'));
    console.error(err);
  };

  const handleGoogleLoginSuccess = async (token: string, clientId: string) => {
    setLoading(true);
    setError('');
    try {
      const response = await fetch('http://localhost:8081/api/oauth/google', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          clientId,
          token,
          provider: 'GOOGLE',
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
      }

      const data = await response.json();
      handleAuthSuccess(data);
    } catch (err) {
      handleAuthError(err);
    } finally {
      setLoading(false);
    }
  };
  
  const handleFacebookLoginSuccess = async (token: string, clientId: string) => {
    setLoading(true);
    setError('');
    try {
      const response = await fetch('http://localhost:8081/api/oauth/facebook', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          clientId,
          token,
          provider: 'FACEBOOK',
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
      }

      const data = await response.json();
      handleAuthSuccess(data);
    } catch (err) {
      handleAuthError(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
  };

  return (
    <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md mx-auto bg-white rounded-lg shadow md:max-w-lg">
        <div className="px-4 py-5 sm:p-6">
          <h1 className="text-2xl font-bold text-center mb-8">Chat-Chit</h1>
          
          {user ? (
            <div className="text-center">
              <h2 className="text-lg font-semibold mb-2">Welcome!</h2>
              <img src={user.avatarUrl} alt="Avatar" className="w-20 h-20 rounded-full mx-auto mb-2"/>
              <p className="text-gray-600 mb-2">{user.email}</p>
              <p className="text-sm text-gray-500 mb-4">User ID: {user.id}</p>
              <button
                onClick={handleLogout}
                className="w-full bg-red-600 text-white py-2 px-4 rounded hover:bg-red-700 transition"
              >
                Logout
              </button>
            </div>
          ) : (
            <div className="space-y-4">
              <div>
                <GoogleLogin 
                  onSuccess={handleGoogleLoginSuccess}
                  onError={() => setError('Google login failed')}
                />
              </div>
              <div>
                <FacebookLogin 
                  onSuccess={handleFacebookLoginSuccess}
                  onError={(err) => setError(typeof err === 'string' ? err : 'Facebook login failed')}
                />
              </div>
              
              {error && (
                <div className="mt-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                  {error}
                </div>
              )}
              
              {loading && (
                <div className="mt-4 text-center text-gray-600">
                  Logging in...
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default App;
