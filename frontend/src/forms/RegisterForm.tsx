import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "../context/AuthContext";
import googleIcon from "../assets/google.svg";
import githubIcon from "../assets/github.svg";

const schema = z
  .object({
    username: z
      .string()
      .min(3, "Username must be at least 3 characters")
      .max(50, "Username too long"),
    email: z.email("Please enter a valid email"),
    password: z
      .string()
      .min(8, "Password must be at least 8 characters")
      .max(50, "Password too long"),
    confirmpassword: z.string(),
  })
  .refine((data) => data.password === data.confirmpassword, {
    message: "Passwords do not match!",
    path: ["confirmpassword"],
  });

type RegisterFormData = z.infer<typeof schema>;

function RegisterForm() {
  const navigate = useNavigate();
  const { register: registerUser } = useAuth();
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(schema),
    mode: "onChange",
  });

  const onSubmit = async (data: RegisterFormData) => {
    setLoading(true);
    setError("");

    try {
      await registerUser(data.username, data.email, data.password);
      navigate("/editor");
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      setError(err.response?.data || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  const handleGitHubLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/github";
  };

  const handleGoogleLogin = () => {
    alert("Google login coming soon!");
  };

  return (
    <div
      className="w-90 bg-neutral-900/80 backdrop-blur-xl border border-white/10 
      flex flex-col gap-6 rounded-2xl p-6 text-white shadow-xl"
    >
      {error && (
        <div className="bg-red-500/20 border border-red-500 text-red-400 px-4 py-2 rounded-lg">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
        <label className="text-sm text-neutral-300">Username</label>
        <input
          {...register("username")}
          type="text"
          className="bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 
            focus:outline-none focus:ring-2 focus:ring-cyan-400"
        />
        {errors.username && (
          <p className="text-red-400 text-xs">{errors.username.message}</p>
        )}

        <label className="text-sm text-neutral-300">Email</label>
        <input
          {...register("email")}
          type="email"
          className="bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 
            focus:outline-none focus:ring-2 focus:ring-cyan-400"
        />
        {errors.email && (
          <p className="text-red-400 text-xs">{errors.email.message}</p>
        )}

        <label className="text-sm text-neutral-300 mt-2">Password</label>
        <input
          {...register("password")}
          type="password"
          className="bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 
            focus:outline-none focus:ring-2 focus:ring-cyan-400"
        />
        {errors.password && (
          <p className="text-red-400 text-xs">{errors.password.message}</p>
        )}

        <label className="text-sm text-neutral-300 mt-2">
          Confirm Password
        </label>
        <input
          {...register("confirmpassword")}
          type="password"
          className="bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 
            focus:outline-none focus:ring-2 focus:ring-cyan-400"
        />
        {errors.confirmpassword && (
          <p className="text-red-400 text-xs">
            {errors.confirmpassword.message}
          </p>
        )}

        <button
          disabled={!isValid || loading}
          className="bg-cyan-500 hover:bg-cyan-400 disabled:opacity-40 disabled:cursor-not-allowed 
            cursor-pointer transition text-black font-semibold py-2 rounded-full"
        >
          {loading ? "Registering..." : "Register"}
        </button>
      </form>

      <div className="flex items-center gap-3 text-neutral-400 text-sm">
        <hr className="flex-1 border-neutral-700" />
        <p>Or Register with</p>
        <hr className="flex-1 border-neutral-700" />
      </div>

      <div className="flex flex-col gap-3">
        <button
          onClick={handleGoogleLogin}
          type="button"
          className="bg-neutral-800 cursor-pointer hover:bg-neutral-700 transition 
          border border-neutral-700 rounded-full py-2 flex items-center justify-around"
        >
          <img className="w-5 h-5" src={googleIcon} alt="Google" />
          <p className="mr-12">Register with Google</p>
        </button>

        <button
          onClick={handleGitHubLogin}
          type="button"
          className="bg-neutral-800 cursor-pointer hover:bg-neutral-700 transition 
          border border-neutral-700 rounded-full py-2 flex justify-around items-center"
        >
          <img className="w-5 h-5" src={githubIcon} alt="GitHub" />
          <p className="mr-12">Register with GitHub</p>
        </button>
      </div>

      <div className="flex items-center gap-3 text-neutral-400 text-sm">
        <hr className="flex-1 border-neutral-700" />
        <p>You have an account?</p>
        <hr className="flex-1 border-neutral-700" />
      </div>

      <button
        onClick={() => navigate("/login")}
        type="button"
        className="border cursor-pointer border-cyan-500 text-cyan-400 hover:bg-cyan-500 
          hover:text-black transition rounded-full py-2"
      >
        Login
      </button>
    </div>
  );
}

export default RegisterForm;
