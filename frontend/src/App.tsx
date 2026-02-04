import CodeEditor from "./Components/CodeEditor";
import Navbar from "./Components/Navbar";
function App() {
  return (
    <div>
      hello world
      <Navbar />
      <CodeEditor language="javascript" defaultValue="......." height="300px" />
    </div>
  );
}

export default App;
