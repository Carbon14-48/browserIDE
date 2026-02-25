import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

function UserMenu() {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
      navigate("/login");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  //prevent from rerendering if there is no user
  if (!isAuthenticated || !user) {
    return null;
  }

  return (
    <div className="bg-neutral-800 border border-neutral-700 rounded-lg p-4 text-white">
      <p className="text-sm text-neutral-400">Logged in as:</p>
      <p className="font-semibold">{user.username}</p>
      <p className="text-sm text-neutral-500">{user.email}</p>

      <button
        onClick={handleLogout}
        className="mt-4 w-full bg-red-500 hover:bg-red-600 transition 
          text-white font-semibold py-2 rounded-lg"
      >
        Logout
      </button>
    </div>
  );
}

export default UserMenu;
