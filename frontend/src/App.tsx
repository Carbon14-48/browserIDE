import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from "react-router-dom";
import FullPageRoute from "./Routes/FullPageRoute";
import Index from "./pages/Index";
import LoginForm from "./forms/LoginForm";
import RegisterForm from "./forms/RegisterForm";

function App() {
  const router = createBrowserRouter(
    createRoutesFromElements(
      <Route path="/" element={<FullPageRoute />}>
        <Route element={<Index />}>
          <Route index element={null} />
          <Route path="login" element={<LoginForm />} />
          <Route path="register" element={<RegisterForm />} />
        </Route>
      </Route>,
    ),
  );

  return <RouterProvider router={router}></RouterProvider>;
}

export default App;
