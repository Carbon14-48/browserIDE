import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
  Navigate,
} from "react-router-dom";
import FullPageRoute from "./Routes/FullPageRoute";
import Index from "./pages/Index";
import LoginForm from "./forms/LoginForm";
import RegisterForm from "./forms/RegisterForm";
import CodeEditor from "./Components/CodeEditor";
import OAuth2Callback from "./pages/OAuth2Callback";
import { useAuth } from "./context/AuthContext";

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();
  if (isLoading) {
    return (
      <div className="min-h-screen bg-neutral-950 flex items-center justify-center">
        <div className="text-white text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-cyan-500 mx-auto mb-4"></div>
          <p>Loading...</p>
        </div>
      </div>
    );
  }

  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
}

function App() {
  const router = createBrowserRouter(
    createRoutesFromElements(
      <Route path="/" element={<FullPageRoute />}>
        <Route element={<Index />}>
          <Route index element={<Navigate to="/login" replace />} />
          <Route path="login" element={<LoginForm />} />
          <Route path="register" element={<RegisterForm />} />
        </Route>
        <Route path="auth/callback" element={<OAuth2Callback />} />
        <Route
          path="editor"
          element={
            <ProtectedRoute>
              <CodeEditor />
            </ProtectedRoute>
          }
        />
      </Route>,
    ),
  );

  return <RouterProvider router={router}></RouterProvider>;
}

export default App;
