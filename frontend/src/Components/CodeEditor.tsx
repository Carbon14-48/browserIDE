import React, { useState } from "react";
import Editor from "@monaco-editor/react";

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
  return (
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
  );
};

export default CodeEditor;
