import Hero from "./components/Hero";
import Features from "./components/Features";
import HowItWorks from "./components/HowItWorks";
import Security from "./components/Security";
import Comparison from "./components/Comparison";
import Download from "./components/Download";
import Footer from "./components/Footer";

export default function Home() {
  return (
    <>
      <Hero />
      <hr className="section-divider" />
      <Features />
      <hr className="section-divider" />
      <HowItWorks />
      <hr className="section-divider" />
      <Security />
      <hr className="section-divider" />
      <Comparison />
      <hr className="section-divider" />
      <Download />
      <Footer />
    </>
  );
}
