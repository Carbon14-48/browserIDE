import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { authService } from "../services/AuthService";
import api, { setAccessToken } from "../services/api";

interface User {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  emailVerified: boolean;
  enabled: boolean;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (
    username: string,
    email: string,
    password: string,
    fullName?: string,
  ) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);
interface AuthProviderProps {
  children: ReactNode;
}
export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const restoreSession = async () => {
      try {
        const refreshResponse = await api.post("/auth/refresh");
        setAccessToken(refreshResponse.data.accessToken);
        const userResponse = await api.get("/auth/me");
        setUser(userResponse.data);
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
      } catch (error) {
        // No valid session - user stays null
        console.log("No session to restore");
      } finally {
        setIsLoading(false);
      }
    };

    restoreSession();
  }, []);

  const login = async (email: string, password: string) => {
    const response = await authService.login({ email, password });
    setUser(response.user);
  };

  const register = async (
    username: string,
    email: string,
    password: string,
    fullname?: string,
  ) => {
    const response = await authService.register({
      username,
      email,
      password,
      fullname,
    });
    setUser(response.user);
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
    setAccessToken(null);
  };
  function LoadingComponent() {
    return (
      <div className="min-h-screen bg-neutral-950 flex items-center justify-center">
        <div className="text-white text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-cyan-500 mx-auto mb-4"></div>
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
      }}
    >
      {isLoading ? <LoadingComponent /> : children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
};
