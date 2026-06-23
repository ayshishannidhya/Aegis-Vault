import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AegisVault-J — Secure Local File Encryption for Windows",
  description:
    "AegisVault-J is a lightweight file encryption vault that protects sensitive files using AES-256-GCM encryption. Free, open-source, and fully local — no cloud, no telemetry.",
  keywords: [
    "file encryption",
    "vault",
    "AES-256",
    "secure storage",
    "Windows",
    "privacy",
    "local encryption",
    "Argon2id",
    "open source",
  ],
  authors: [{ name: "Ayshi Shannidhya Panda" }],
  openGraph: {
    title: "AegisVault-J — Secure Local File Encryption",
    description:
      "A lightweight file encryption vault for Windows. Protect sensitive files with AES-256-GCM encryption.",
    type: "website",
    locale: "en_US",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link
          rel="preconnect"
          href="https://fonts.gstatic.com"
          crossOrigin="anonymous"
        />
        <link
          href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap"
          rel="stylesheet"
        />
        <link
          rel="icon"
          type="image/svg+xml"
          href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 64 64'%3E%3Cdefs%3E%3ClinearGradient id='g' x1='0' y1='0' x2='0' y2='1'%3E%3Cstop offset='0%25' stop-color='%2300e5ff'/%3E%3Cstop offset='100%25' stop-color='%230a84ff'/%3E%3C/linearGradient%3E%3C/defs%3E%3Cpath d='M32 4L8 16v16c0 14.4 10.2 27.8 24 32 13.8-4.2 24-17.6 24-32V16L32 4z' fill='%230d1525' stroke='url(%23g)' stroke-width='2.5'/%3E%3Crect x='24' y='26' width='16' height='14' rx='2' fill='none' stroke='url(%23g)' stroke-width='2'/%3E%3Crect x='27' y='20' width='10' height='10' rx='5' fill='none' stroke='url(%23g)' stroke-width='2'/%3E%3Ccircle cx='32' cy='33' r='2' fill='url(%23g)'/%3E%3C/svg%3E"
        />
      </head>
      <body>{children}</body>
    </html>
  );
}
