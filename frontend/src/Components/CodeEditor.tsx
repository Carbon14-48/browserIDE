import React, { useState } from "react";
import Editor from "@monaco-editor/react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

interface MonacoEditorProps {
  language?: string;
  defaultValue?: string;
  height?: string;
  width?: string;
}

const CodeEditor: React.FC<MonacoEditorProps> = ({
  language = "javascript",
  defaultValue = "// Start coding...",
  height = "400px",
  width = "100%",
}) => {
  const [value, setValue] = useState(defaultValue);
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [testMessage, setTestMessage] = useState("");

  const handleLogout = async () => {
    try {
      await logout();
      navigate("/login");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  const testProtectedEndpoint = async () => {
    try {
      const response = await api.get("/test/protected");
      setTestMessage(`${response.data}`);
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      setTestMessage("Failed to access endpoint");
    }
  };

  const resendVerificationEmail = async () => {
    if (!user?.email) return;

    try {
      await api.post(`/email/resend?email=${user.email}`);
      setTestMessage("Verification email sent! Check your inbox.");
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      setTestMessage("Failed to send verification email");
    }
  };

  return (
    <div className="min-h-screen bg-neutral-950 p-4">
      {/* User Menu Bar */}
      {isAuthenticated && user && (
        <div className="bg-neutral-800 border border-neutral-700 rounded-lg p-4 mb-4">
          <div className="flex justify-between items-center">
            <div className="text-white">
              <p className="text-sm text-neutral-400">Logged in as:</p>
              <p className="font-semibold">
                {user.username} ({user.email})
              </p>
            </div>

            <div className="flex gap-2">
              <button
                onClick={testProtectedEndpoint}
                className="bg-cyan-500 hover:bg-cyan-600 transition text-black font-semibold px-6 py-2 rounded-lg"
              >
                Test API
              </button>

              <button
                onClick={handleLogout}
                className="bg-red-500 hover:bg-red-600 transition text-white font-semibold px-6 py-2 rounded-lg"
              >
                Logout
              </button>
            </div>
          </div>

          {testMessage && (
            <p className="mt-4 text-sm text-white bg-neutral-900 p-2 rounded">
              {testMessage}
            </p>
          )}
        </div>
      )}

      {/* Email Verification Banner - ADD THIS! */}
      {isAuthenticated && user && !user.emailVerified && (
        <div className="bg-yellow-500/20 border border-yellow-500 rounded-lg p-4 mb-4">
          <div className="flex items-center justify-between">
            <div className="text-yellow-400">
              <p className="font-semibold flex items-center gap-2">
                ⚠️ Email Not Verified
              </p>
              <p className="text-sm mt-1">
                Please check your inbox and verify your email address.
              </p>
            </div>
            <button
              onClick={resendVerificationEmail}
              className="bg-yellow-500 hover:bg-yellow-400 transition text-black px-4 py-2 rounded-lg text-sm font-semibold whitespace-nowrap"
            >
              Resend Email
            </button>
          </div>
        </div>
      )}

      {/* Editor */}
      <div style={{ width, border: "1px solid #ddd", borderRadius: "4px" }}>
        <Editor
          height={height}
          language={language}
          value={value}
          onChange={(newValue) => setValue(newValue || "")}
          theme="vs-dark"
          options={{
            fontSize: 14,
            minimap: { enabled: true },
            wordWrap: "on",
            formatOnPaste: true,
            formatOnType: true,
          }}
        />
      </div>
    </div>
  );
};

export default CodeEditor;
