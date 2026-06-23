"use client";

import { motion } from "framer-motion";
import { useInView } from "framer-motion";
import { useRef } from "react";

const features = [
    {
        icon: "🔐",
        title: "AES-256-GCM Encryption",
        description:
            "Industry-standard authenticated encryption that protects both data confidentiality and integrity. Every file is individually encrypted.",
    },
    {
        icon: "🔑",
        title: "Argon2id Key Derivation",
        description:
            "Winner of the Password Hashing Competition. Memory-hard and GPU-resistant — brute-force attacks are computationally infeasible.",
    },
    {
        icon: "⏱️",
        title: "Auto-Lock Protection",
        description:
            "Configurable inactivity timeout (1–60 minutes) automatically locks your vault  and wipes encryption keys from memory.",
    },
    {
        icon: "📂",
        title: "Import & Export Workflow",
        description:
            "Securely import files into the encrypted vault. Export when needed. Clear security boundaries — you always know what's protected.",
    },
    {
        icon: "💻",
        title: "Cross-Platform",
        description:
            "Native installers for Windows (.exe), Linux (.deb/.rpm), and macOS (.dmg). Bundled Java runtime — no extra installs required.",
    },
    {
        icon: "💪",
        title: "Password Strength Meter",
        description:
            "Real-time entropy-based password strength indicator. Warns against weak passwords before they can compromise your vault.",
    },
];

export default function Features() {
    const ref = useRef(null);
    const isInView = useInView(ref, { once: true, margin: "-80px" });

    return (
        <section id="features" ref={ref}>
            <div className="container">
                <div className="text-center" style={{ marginBottom: "3rem" }}>
                    <motion.span
                        className="section-label"
                        initial={{ opacity: 0, y: 10 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5 }}
                    >
                        Features
                    </motion.span>
                    <motion.h2
                        className="section-title"
                        initial={{ opacity: 0, y: 15 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.1 }}
                    >
                        Built With Security First
                    </motion.h2>
                    <motion.p
                        className="section-subtitle mx-auto"
                        initial={{ opacity: 0, y: 15 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.2 }}
                    >
                        Every design decision prioritizes the protection of your files.
                        No shortcuts, no compromises.
                    </motion.p>
                </div>

                <div className="grid-3">
                    {features.map((feature, i) => (
                        <motion.div
                            key={feature.title}
                            className="card"
                            initial={{ opacity: 0, y: 25 }}
                            animate={isInView ? { opacity: 1, y: 0 } : {}}
                            transition={{ duration: 0.5, delay: 0.1 + i * 0.08 }}
                        >
                            <div className="card-icon">{feature.icon}</div>
                            <h3>{feature.title}</h3>
                            <p>{feature.description}</p>
                        </motion.div>
                    ))}
                </div>
            </div>
        </section>
    );
}
