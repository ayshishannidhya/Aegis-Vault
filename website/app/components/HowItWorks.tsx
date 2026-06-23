"use client";

import { motion, useInView } from "framer-motion";
import { useRef } from "react";

const steps = [
    {
        num: 1,
        title: "Create a Vault",
        desc: "Create a new encrypted .avj container file and set a strong password. Your vault is ready.",
    },
    {
        num: 2,
        title: "Import Files",
        desc: "Select files or entire folders. They're encrypted and stored securely inside the vault.",
    },
    {
        num: 3,
        title: "Browse & Manage",
        desc: "View your files, create folders, search, rename, and organize — all within the encrypted vault.",
    },
    {
        num: 4,
        title: "Export When Needed",
        desc: "Decrypt and export files to a chosen location. Delete them when done for full security.",
    },
];

export default function HowItWorks() {
    const ref = useRef(null);
    const isInView = useInView(ref, { once: true, margin: "-80px" });

    return (
        <section id="how-it-works" ref={ref}>
            <div className="container">
                <div className="text-center" style={{ marginBottom: "3rem" }}>
                    <motion.span
                        className="section-label"
                        initial={{ opacity: 0, y: 10 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5 }}
                    >
                        How It Works
                    </motion.span>
                    <motion.h2
                        className="section-title"
                        initial={{ opacity: 0, y: 15 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.1 }}
                    >
                        Simple, Deliberate Security
                    </motion.h2>
                    <motion.p
                        className="section-subtitle mx-auto"
                        initial={{ opacity: 0, y: 15 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.2 }}
                    >
                        AegisVault-J uses an intentional import-export model. You always
                        know exactly when files enter and leave the protected vault.
                    </motion.p>
                </div>

                <div className="grid-4">
                    {steps.map((step, i) => (
                        <motion.div
                            key={step.num}
                            className="card step-card"
                            initial={{ opacity: 0, y: 25 }}
                            animate={isInView ? { opacity: 1, y: 0 } : {}}
                            transition={{ duration: 0.5, delay: 0.15 + i * 0.1 }}
                        >
                            <div className="step-number">{step.num}</div>
                            <h3>{step.title}</h3>
                            <p>{step.desc}</p>
                        </motion.div>
                    ))}
                </div>

                {/* Workflow diagram */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.6, delay: 0.6 }}
                    style={{
                        marginTop: "2.5rem",
                        textAlign: "center",
                        padding: "1.5rem",
                        background: "var(--bg-card)",
                        border: "1px solid var(--border)",
                        borderRadius: "var(--radius)",
                        backdropFilter: "blur(12px)",
                    }}
                >
                    <p
                        style={{
                            fontSize: "0.85rem",
                            color: "var(--text-muted)",
                            fontFamily: "'SF Mono', 'Cascadia Code', 'Fira Code', monospace",
                            letterSpacing: "0.02em",
                        }}
                    >
                        Import → Encrypt → Lock → Unlock → Export → Use → Delete →
                        Re-Import
                    </p>
                </motion.div>
            </div>
        </section>
    );
}
