"use client";

import { motion, useInView } from "framer-motion";
import { useRef, useCallback } from "react";

export default function Download() {
    const ref = useRef(null);
    const isInView = useInView(ref, { once: true, margin: "-80px" });

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
        <section id="download" ref={ref} className="download-section">
            <div className="container">
                <motion.span
                    className="section-label"
                    initial={{ opacity: 0, y: 10 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.5 }}
                >
                    Download
                </motion.span>

                <motion.h2
                    className="section-title"
                    initial={{ opacity: 0, y: 15 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.5, delay: 0.1 }}
                    style={{ marginBottom: "0.5rem" }}
                >
                    Ready to Protect Your Files?
                </motion.h2>

                <motion.p
                    className="section-subtitle mx-auto"
                    initial={{ opacity: 0, y: 15 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.5, delay: 0.2 }}
                    style={{ marginBottom: "2rem" }}
                >
                    Download AegisVault-J for free. No account required, no telemetry,
                    no strings attached.
                </motion.p>

                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.5, delay: 0.3 }}
                >
                    <a
                        href="/downloads/AegisVault-J-Setup.exe"
                        download
                        className="btn-primary"
                        onClick={handleClick}
                        style={{ fontSize: "1.05rem", padding: "1rem 2.75rem" }}
                    >
                        <svg
                            width="20"
                            height="20"
                            viewBox="0 0 24 24"
                            fill="currentColor"
                        >
                            <path d="M12 16l-5-5h3V4h4v7h3l-5 5z" />
                            <path d="M20 18H4v2h16v-2z" />
                        </svg>
                        Download for Windows
                    </a>
                    <p className="platform-note">
                        Windows 10 &amp; 11 (64-bit) · No Java required
                    </p>
                </motion.div>

                {/* System Requirements */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.5, delay: 0.4 }}
                    style={{
                        marginTop: "2.5rem",
                        display: "flex",
                        justifyContent: "center",
                        gap: "2.5rem",
                        flexWrap: "wrap",
                    }}
                >
                    {[
                        { label: "Min RAM", value: "512 MB" },
                        { label: "Disk Space", value: "200 MB" },
                        { label: "Display", value: "1280×720" },
                        { label: "Runtime", value: "Bundled" },
                    ].map((req) => (
                        <div key={req.label} style={{ textAlign: "center" }}>
                            <div
                                style={{
                                    fontSize: "1.05rem",
                                    fontWeight: 700,
                                    background:
                                        "linear-gradient(135deg, #c084fc, #f59e0b)",
                                    WebkitBackgroundClip: "text",
                                    WebkitTextFillColor: "transparent",
                                    backgroundClip: "text",
                                }}
                            >
                                {req.value}
                            </div>
                            <div
                                style={{
                                    fontSize: "0.72rem",
                                    color: "var(--text-muted)",
                                    textTransform: "uppercase",
                                    letterSpacing: "0.06em",
                                    marginTop: "0.2rem",
                                }}
                            >
                                {req.label}
                            </div>
                        </div>
                    ))}
                </motion.div>

                {/* Version Info */}
                <motion.div
                    initial={{ opacity: 0, y: 15 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.5, delay: 0.5 }}
                    style={{
                        marginTop: "2rem",
                        display: "flex",
                        justifyContent: "center",
                    }}
                >
                    <div className="download-meta">
                        <span>
                            <span style={{ color: "var(--text-muted)" }}>Version </span>
                            <span className="meta-value">v1.0.0</span>
                        </span>
                        <span>
                            <span style={{ color: "var(--text-muted)" }}>Size </span>
                            <span className="meta-value">~45 MB</span>
                        </span>
                        <span className="checksum-line">
                            <span style={{ color: "var(--text-muted)" }}>SHA-256 </span>
                            <span className="meta-value">
                                e3b0c44298fc1c149afbf4c8...placeholder
                            </span>
                        </span>
                    </div>
                </motion.div>
            </div>
        </section>
    );
}
