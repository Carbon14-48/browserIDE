import googleIcon from "../assets/google.svg";
import githubIcon from "../assets/github.svg";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

const schema = z.object({
  email: z.email("Please enter a valid email"),
  password: z
    .string()
    .min(4, "Password too short")
    .max(12, "Password too long"),
});

type LoginFormData = z.infer<typeof schema>;

function LoginForm() {
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
  } = useForm<LoginFormData>({
    resolver: zodResolver(schema),
    mode: "onChange",
  });

  const onSubmit = (data: LoginFormData) => {
    console.log("Login data:", data);
  };

  return (
    <div
      className="w-90 bg-neutral-900/80 backdrop-blur-xl border border-white/10 
      flex flex-col gap-6 rounded-2xl p-6 text-white shadow-xl"
    >
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
        <div className="flex flex-col gap-1">
          <label className="text-sm text-neutral-300">Email</label>
          <input
            type="email"
            {...register("email")}
            placeholder="Enter your email"
            className="bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 
              focus:outline-none focus:ring-2 focus:ring-cyan-400"
          />
          {errors.email && (
            <p className="text-red-400 text-xs">{errors.email.message}</p>
          )}

          <label className="text-sm text-neutral-300 mt-2">Password</label>
          <input
            type="password"
            {...register("password")}
            placeholder="Enter your password"
            className="bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 
              focus:outline-none focus:ring-2 focus:ring-cyan-400"
          />
          {errors.password && (
            <p className="text-red-400 text-xs">{errors.password.message}</p>
          )}
        </div>

        <button
          disabled={!isValid}
          className="bg-cyan-500 disabled:opacity-40 hover:bg-cyan-400 transition 
            text-black font-semibold py-2 rounded-full"
        >
          Login
        </button>
      </form>

      <div className="flex items-center gap-3 text-neutral-400 text-sm">
        <hr className="flex-1 border-neutral-700" />
        <p>Or sign in with</p>
        <hr className="flex-1 border-neutral-700" />
      </div>

      <div className="flex flex-col gap-3">
        <button
          className="bg-neutral-800 hover:bg-neutral-700 transition 
          border border-neutral-700 rounded-full py-2 flex items-center justify-around"
        >
          <img className="w-5 h-5" src={googleIcon} />
          <p className="mr-12">Login with Google</p>
        </button>

        <button
          className="bg-neutral-800 hover:bg-neutral-700 transition 
          border border-neutral-700 rounded-full py-2 flex items-center justify-around"
        >
          <img className="w-5 h-5" src={githubIcon} />
          <p className="mr-12">Login with GitHub</p>
        </button>
      </div>

      <div className="flex items-center gap-3 text-neutral-400 text-sm">
        <hr className="flex-1 border-neutral-700" />
        <p>No account?</p>
        <hr className="flex-1 border-neutral-700" />
      </div>

      <button
        onClick={() => navigate("/register")}
        className="border border-cyan-500 text-cyan-400 hover:bg-cyan-500 
          hover:text-black transition rounded-full py-2"
      >
        Register
      </button>
    </div>
  );
}

export default LoginForm;
