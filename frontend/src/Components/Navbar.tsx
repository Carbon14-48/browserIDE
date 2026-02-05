export default function Navbar() {
  return (
    <nav className="absolute top-6 left-1/2 transform -translate-x-1/2 w-[90%] max-w-4xl z-30">
      <div className="bg-black/50 backdrop-blur-md rounded-full px-6 py-3 flex items-center justify-between text-white border border-white/5 glass">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 flex items-center justify-center bg-white/10 rounded-full">
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              className="text-white"
            >
              <path d="M12 2L15 8H9L12 2Z" fill="currentColor" />
            </svg>
          </div>
          <span className="font-medium">Glitch</span>
        </div>
        <div className="flex items-center gap-6 text-sm">
          <a className="opacity-80 hover:opacity-100 cursor-pointer" href="#">
            Home
          </a>
          <a className="opacity-80 hover:opacity-100 cursor-pointer" href="#">
            Docs
          </a>
        </div>
      </div>
    </nav>
  );
}
