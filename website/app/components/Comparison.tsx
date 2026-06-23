"use client";

import { motion, useInView } from "framer-motion";
import { useRef } from "react";

const rows = [
    {
        feature: "Encryption",
        aegis: "AES-256-GCM",
        veracrypt: "AES / Serpent / Twofish",
        cryptomator: "AES-256",
        axcrypt: "AES-256 (paid)",
        sevenzip: "AES-256",
    },
    {
        feature: "Key Derivation",
        aegis: "Argon2id",
        veracrypt: "PBKDF2-SHA512",
        cryptomator: "scrypt",
        axcrypt: "PBKDF2",
        sevenzip: "SHA-256 based",
    },
    {
        feature: "Type",
        aegis: "File container",
        veracrypt: "Disk / container",
        cryptomator: "Cloud vault",
        axcrypt: "File encryption",
        sevenzip: "Archive",
    },
    {
        feature: "Open Source",
        aegis: "check",
        veracrypt: "check",
        cryptomator: "check",
        axcrypt: "cross",
        sevenzip: "check",
    },
    {
        feature: "No Driver Required",
        aegis: "check",
        veracrypt: "cross",
        cryptomator: "check",
        axcrypt: "check",
        sevenzip: "check",
    },
    {
        feature: "Bundled Runtime",
        aegis: "check",
        veracrypt: "cross",
        cryptomator: "cross",
        axcrypt: "cross",
        sevenzip: "cross",
    },
    {
        feature: "Full Disk Encryption",
        aegis: "cross",
        veracrypt: "check",
        cryptomator: "cross",
        axcrypt: "cross",
        sevenzip: "cross",
    },
    {
        feature: "Cloud Integration",
        aegis: "cross",
        veracrypt: "cross",
        cryptomator: "check",
        axcrypt: "check",
        sevenzip: "cross",
    },
    {
        feature: "Hidden Volumes",
        aegis: "cross",
        veracrypt: "check",
        cryptomator: "cross",
        axcrypt: "cross",
        sevenzip: "cross",
    },
    {
        feature: "Portable File",
        aegis: "check",
        veracrypt: "check",
        cryptomator: "cross",
        axcrypt: "cross",
        sevenzip: "check",
    },
    {
        feature: "Cross-Platform",
        aegis: "check",
        veracrypt: "check",
        cryptomator: "check",
        axcrypt: "check",
        sevenzip: "partial",
    },
    {
        feature: "Free to Use",
        aegis: "check",
        veracrypt: "check",
        cryptomator: "check",
        axcrypt: "partial",
        sevenzip: "check",
    },
];

function CellValue({ value }: { value: string }) {
    if (value === "check") return <span className="check">✓</span>;
    if (value === "cross") return <span className="cross">✗</span>;
    if (value === "partial") return <span className="partial">◐</span>;
    return <span>{value}</span>;
}

export default function Comparison() {
    const ref = useRef(null);
    const isInView = useInView(ref, { once: true, margin: "-80px" });

    return (
        <section id="comparison" ref={ref}>
            <div className="container">
                <div className="text-center" style={{ marginBottom: "3rem" }}>
                    <motion.span
                        className="section-label"
                        initial={{ opacity: 0, y: 10 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5 }}
                    >
                        Comparison
                    </motion.span>
                    <motion.h2
                        className="section-title"
                        initial={{ opacity: 0, y: 15 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.1 }}
                    >
                        How Does It Stack Up?
                    </motion.h2>
                    <motion.p
                        className="section-subtitle mx-auto"
                        initial={{ opacity: 0, y: 15 }}
                        animate={isInView ? { opacity: 1, y: 0 } : {}}
                        transition={{ duration: 0.5, delay: 0.2 }}
                    >
                        Every tool has its strengths. AegisVault-J excels at simple,
                        portable, local file encryption with modern cryptography.
                    </motion.p>
                </div>

                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={isInView ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 0.6, delay: 0.3 }}
                    className="comparison-table-wrapper"
                >
                    <table className="comparison-table">
                        <thead>
                            <tr>
                                <th>Feature</th>
                                <th className="highlight">AegisVault-J</th>
                                <th>VeraCrypt</th>
                                <th>Cryptomator</th>
                                <th>AxCrypt</th>
                                <th>7-Zip</th>
                            </tr>
                        </thead>
                        <tbody>
                            {rows.map((row) => (
                                <tr key={row.feature}>
                                    <td>{row.feature}</td>
                                    <td className="highlight">
                                        <CellValue value={row.aegis} />
                                    </td>
                                    <td>
                                        <CellValue value={row.veracrypt} />
                                    </td>
                                    <td>
                                        <CellValue value={row.cryptomator} />
                                    </td>
                                    <td>
                                        <CellValue value={row.axcrypt} />
                                    </td>
                                    <td>
                                        <CellValue value={row.sevenzip} />
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </motion.div>

                <motion.p
                    initial={{ opacity: 0 }}
                    animate={isInView ? { opacity: 1 } : {}}
                    transition={{ duration: 0.5, delay: 0.5 }}
                    style={{
                        textAlign: "center",
                        fontSize: "0.78rem",
                        color: "var(--text-muted)",
                        marginTop: "1rem",
                    }}
                >
                    ◐ = Partial support &nbsp;·&nbsp; AegisVault-J is designed for local
                    file container encryption — not disk encryption or cloud sync.
                </motion.p>
            </div>
        </section>
    );
}
