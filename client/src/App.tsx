import { useState } from 'react'
import './App.css'
import GoogleLogin from './components/GoogleLogin'

function App() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [user, setUser] = useState<any>(null)

  const handleGoogleLoginSuccess = async (idToken: string, clientId: string) => {
    setLoading(true)
    setError('')
    try {
      const response = await fetch('http://localhost:8081/api/oauth/google', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          clientId,
          idToken,
        }),
      })

      if (!response.ok) {
        throw new Error('Login failed')
      }

      const data = await response.json()
      console.log('Login success:', data)
      
      // Lưu token
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      
      // Lưu user info
      setUser(data)
    } catch (err) {
      setError('Login failed: ' + (err instanceof Error ? err.message : 'Unknown error'))
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    setUser(null)
  }

  return (
    <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md mx-auto bg-white rounded-lg shadow md:max-w-lg">
        <div className="px-4 py-5 sm:p-6">
          <h1 className="text-2xl font-bold text-center mb-8">Chat-Chit</h1>
          
          {user ? (
            <div className="text-center">
              <h2 className="text-lg font-semibold mb-2">Welcome!</h2>
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
            <div>
              <div className="mb-4">
                <GoogleLogin 
                  onSuccess={handleGoogleLoginSuccess}
                  onError={() => setError('Google login failed')}
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

export default App
