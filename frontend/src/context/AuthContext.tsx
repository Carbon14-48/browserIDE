import { createContext, useContext, useState, type ReactNode } from "react";
import { authService } from "../services/AuthService";
import { setAccessToken } from "../services/api";

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

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);

  const login = async (email: string, password: string) => {
    const response = await authService.login({ email, password });
    setUser(response.user);
    // Access token stored in memory via api.ts
    // Refresh token stored in HttpOnly cookie automatically
  };

  const register = async (
    username: string,
    email: string,
    password: string,
    fullName?: string,
  ) => {
    const response = await authService.register({
      username,
      email,
      password,
      fullName,
    });
    setUser(response.user);
    // Access token stored in memory via api.ts
    // Refresh token stored in HttpOnly cookie automatically
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
    setAccessToken(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
};
