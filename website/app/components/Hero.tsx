"use client";

import { motion } from "framer-motion";
import { useCallback } from "react";

export default function Hero() {
    const handleClick = useCallback((e: React.MouseEvent<HTMLAnchorElement>) => {
        const btn = e.currentTarget;
        const rect = btn.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        const ripple = document.createElement("span");
        ripple.classList.add("ripple");
        ripple.style.width = ripple.style.height = size + "px";
        ripple.style.left = e.clientX - rect.left - size / 2 + "px";
        ripple.style.top = e.clientY - rect.top - size / 2 + "px";
        btn.appendChild(ripple);
        ripple.addEventListener("animationend", () => ripple.remove());
    }, []);

    return (
        <section
            style={{
                minHeight: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                textAlign: "center",
                padding: "2rem 1.5rem",
            }}
        >
            <div style={{ maxWidth: 620 }}>
                {/* Animated Lock */}
                <motion.div
                    initial={{ opacity: 0, scale: 0.7 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.7, ease: "easeOut" }}
                    className="lock-container"
                >
                    <div className="lock-glow" />
                    <div className="lock-shackle" />
                    <div className="lock-body">
                        <div className="lock-keyhole" />
                    </div>
                    {/* Floating key particles */}
                    <span className="key-particle" style={{ top: "8%", left: "8%" }}>🔑</span>
                    <span className="key-particle" style={{ top: "15%", right: "5%" }}>✦</span>
                    <span className="key-particle" style={{ bottom: "35%", left: "2%" }}>⚿</span>
                    <span className="key-particle" style={{ bottom: "8%", right: "12%" }}>✦</span>
                </motion.div>

                {/* Title */}
                <motion.h1
                    initial={{ opacity: 0, y: 24 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.6, delay: 0.2 }}
                    style={{
                        fontSize: "clamp(2.8rem, 6vw, 4.2rem)",
                        fontWeight: 800,
                        letterSpacing: "-0.04em",
                        background:
                            "linear-gradient(135deg, #f0eef6 20%, #a855f7 60%, #ec4899 100%)",
                        WebkitBackgroundClip: "text",
                        WebkitTextFillColor: "transparent",
                        backgroundClip: "text",
                        marginBottom: "0.75rem",
                        lineHeight: 1.05,
                    }}
                >
                    AegisVault-J
                </motion.h1>

                {/* Tagline */}
                <motion.p
                    initial={{ opacity: 0, y: 24 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.6, delay: 0.35 }}
                    style={{
                        fontSize: "clamp(1rem, 2.5vw, 1.25rem)",
                        background:
                            "linear-gradient(90deg, #c084fc, #f59e0b)",
                        WebkitBackgroundClip: "text",
                        WebkitTextFillColor: "transparent",
                        backgroundClip: "text",
                        fontWeight: 500,
                        marginBottom: "1.5rem",
                    }}
                >
                    Secure Local File Encryption for Windows
                </motion.p>

                {/* Description */}
                <motion.p
                    initial={{ opacity: 0, y: 24 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.6, delay: 0.5 }}
                    style={{
                        fontSize: "1rem",
                        color: "#a8a3c0",
                        lineHeight: 1.75,
                        maxWidth: 500,
                        margin: "0 auto 2.5rem",
                    }}
                >
                    A lightweight file encryption vault that protects sensitive files
                    using AES-256-GCM encryption. Open source, fully local — no cloud,
                    no telemetry, no compromise.
                </motion.p>

                {/* Download Button */}
                <motion.div
                    initial={{ opacity: 0, y: 24 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.6, delay: 0.65 }}
                >
                    <a
                        href="/downloads/AegisVault-J-Setup.exe"
                        download
                        className="btn-primary"
                        onClick={handleClick}
                    >
                        <svg
                            width="18"
                            height="18"
                            viewBox="0 0 24 24"
                            fill="currentColor"
                        >
                            <path d="M12 16l-5-5h3V4h4v7h3l-5 5z" />
                            <path d="M20 18H4v2h16v-2z" />
                        </svg>
                        Download for Windows
                    </a>
                    <p
                        style={{
                            fontSize: "0.82rem",
                            color: "#6e6891",
                            marginTop: "0.75rem",
                        }}
                    >
                        Windows 10 &amp; 11 (64-bit) · Free &amp; Open Source
                    </p>
                </motion.div>

                {/* Scroll hint */}
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 0.4 }}
                    transition={{ delay: 1.8, duration: 1 }}
                    style={{ marginTop: "4rem" }}
                >
                    <motion.div
                        animate={{ y: [0, 10, 0] }}
                        transition={{
                            repeat: Infinity,
                            duration: 2.5,
                            ease: "easeInOut",
                        }}
                    >
                        <svg
                            width="22"
                            height="22"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="#6e6891"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                        >
                            <polyline points="6 9 12 15 18 9" />
                        </svg>
                    </motion.div>
                </motion.div>
            </div>
        </section>
    );
}
