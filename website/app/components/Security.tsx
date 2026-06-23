"use client";

import { motion, useInView } from "framer-motion";
import { useRef } from "react";

const specs = [
    { value: "AES-256", label: "Encryption" },
    { value: "GCM", label: "Auth Mode" },
    { value: "Argon2id", label: "Key Derivation" },
    { value: "96-bit", label: "IV/Nonce" },
    { value: "128-bit", label: "Auth Tag" },
    { value: "256-bit", label: "Salt" },
];

const protectedItems = [
    { icon: "✅", text: "Vault file at rest (locked)" },
    { icon: "✅", text: "Vault on USB or external storage" },
    { icon: "✅", text: "Computer stolen while locked" },
    { icon: "✅", text: "Brute-force password attacks" },
    { icon: "✅", text: "Data tampering (GCM detects)" },
];

const unprotectedItems = [
    { icon: "⚠️", text: "Exported files on disk" },
    { icon: "⚠️", text: "Unlocked vault + system access" },
    { icon: "⚠️", text: "Keyloggers & malware" },
    { icon: "⚠️", text: "Forgotten passwords (no recovery)" },
];

export default function Security() {
    const ref = useRef(null);
    const isInView = useInView(ref, { once: true, margin: "-80px" });

    return (
        <section id="security" ref={ref}>
            <div className="container">
                <div className="text-center" style={{ marginBottom: "3rem" }}>
                    <motion.span
                        className="section-label"
                        initial={{ opacity: 0, y: 10 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5 }}
                    >
                        Security
                    </motion.span>
                    <motion.h2
                        className="section-title"
                        initial={{ opacity: 0, y: 15 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.1 }}
                    >
                        Honest Security, No Buzzwords
                    </motion.h2>
                    <motion.p
                        className="section-subtitle mx-auto"
                        initial={{ opacity: 0, y: 15 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.2 }}
                    >
                        We tell you exactly what AegisVault-J protects — and what it
                        doesn&apos;t. No &quot;military-grade&quot; marketing.
                    </motion.p>
                </div>

                {/* Crypto Specs */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.5, delay: 0.3 }}
                >
                    <div className="spec-grid">
                        {specs.map((spec, i) => (
                            <motion.div
                                key={spec.label}
                                className="spec-item"
                                initial={{ opacity: 0, y: 15 }}
                                animate={isInView ? { opacity: 1, y: 0 } : {}}
                                transition={{ duration: 0.4, delay: 0.35 + i * 0.06 }}
                            >
                                <div className="spec-value">{spec.value}</div>
                                <div className="spec-label">{spec.label}</div>
                            </motion.div>
                        ))}
                    </div>
                </motion.div>

                {/* Protection Matrix */}
                <div className="grid-2" style={{ marginTop: "2.5rem" }}>
                    <motion.div
                        initial={{ opacity: 0, x: -20 }}
                        animate={isInView ? { opacity: 1, x: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.5 }}
                    >
                        <h3
                            style={{
                                fontSize: "1rem",
                                fontWeight: 600,
                                marginBottom: "1rem",
                                color: "#34d399",
                            }}
                        >
                            🛡️ What IS Protected
                        </h3>
                        <div
                            style={{
                                display: "flex",
                                flexDirection: "column",
                                gap: "0.6rem",
                            }}
                        >
                            {protectedItems.map((item) => (
                                <div key={item.text} className="badge protected">
                                    <span className="badge-icon">{item.icon}</span>
                                    <span>{item.text}</span>
                                </div>
                            ))}
                        </div>
                    </motion.div>

                    <motion.div
                        initial={{ opacity: 0, x: 20 }}
                        animate={isInView ? { opacity: 1, x: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.6 }}
                    >
                        <h3
                            style={{
                                fontSize: "1rem",
                                fontWeight: 600,
                                marginBottom: "1rem",
                                color: "#f87171",
                                opacity: 0.9,
                            }}
                        >
                            ⚡ What is NOT Protected
                        </h3>
                        <div
                            style={{
                                display: "flex",
                                flexDirection: "column",
                                gap: "0.6rem",
                            }}
                        >
                            {unprotectedItems.map((item) => (
                                <div key={item.text} className="badge unprotected">
                                    <span className="badge-icon">{item.icon}</span>
                                    <span>{item.text}</span>
                                </div>
                            ))}
                        </div>
                    </motion.div>
                </div>

                {/* Architecture */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.6, delay: 0.7 }}
                    style={{
                        marginTop: "2.5rem",
                        padding: "2rem",
                        background: "var(--bg-card)",
                        border: "1px solid var(--border)",
                        borderRadius: "var(--radius)",
                        backdropFilter: "blur(16px)",
                    }}
                >
                    <h3
                        style={{
                            fontSize: "0.95rem",
                            fontWeight: 600,
                            marginBottom: "1.25rem",
                            textAlign: "center",
                            color: "var(--text-secondary)",
                        }}
                    >
                        🏗️ Architecture Layers
                    </h3>
                    <div
                        style={{
                            fontFamily:
                                "'SF Mono', 'Cascadia Code', 'Fira Code', monospace",
                            fontSize: "0.78rem",
                            color: "var(--text-secondary)",
                            textAlign: "center",
                            lineHeight: 2.2,
                        }}
                    >
                        {[
                            {
                                layer: "JavaFX UI",
                                role: "Presentation — never handles keys",
                            },
                            {
                                layer: "Vault Service",
                                role: "Orchestration & business logic",
                            },
                            {
                                layer: "Virtual File System",
                                role: "Logical file/folder abstraction",
                            },
                            {
                                layer: "Container Engine",
                                role: "Crypto operations & block I/O",
                            },
                            { layer: "Vault File (.avj)", role: "Encrypted at rest" },
                        ].map((item, i) => (
                            <motion.div
                                key={item.layer}
                                initial={{ opacity: 0, x: -10 }}
                                animate={isInView ? { opacity: 1, x: 0 } : {}}
                                transition={{ duration: 0.3, delay: 0.8 + i * 0.08 }}
                                style={{
                                    padding: "0.55rem 1rem",
                                    borderRadius: "8px",
                                    background: `rgba(168, 85, 247, ${0.04 + i * 0.02})`,
                                    border: "1px solid var(--border)",
                                    marginBottom: i < 4 ? "0.4rem" : 0,
                                    transition: "all 0.2s ease",
                                    cursor: "default",
                                }}
                                whileHover={{
                                    x: 4,
                                    borderColor: "rgba(168,85,247,0.3)",
                                }}
                            >
                                <span
                                    style={{
                                        background:
                                            "linear-gradient(90deg, #a855f7, #ec4899)",
                                        WebkitBackgroundClip: "text",
                                        WebkitTextFillColor: "transparent",
                                        backgroundClip: "text",
                                        fontWeight: 600,
                                    }}
                                >
                                    {item.layer}
                                </span>
                                <span
                                    style={{
                                        color: "var(--text-muted)",
                                        marginLeft: "0.75rem",
                                    }}
                                >
                                    — {item.role}
                                </span>
                            </motion.div>
                        ))}
                    </div>
                </motion.div>
            </div>
        </section>
    );
}
