import { Outlet } from "react-router-dom";
import Navbar from "../Components/Navbar";
function NavbarRoute() {
  return (
    <div>
      <Navbar />
      <Outlet />
    </div>
  );
}

export default NavbarRoute;
