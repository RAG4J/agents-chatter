/** @type {import('next').NextConfig} */
const nextConfig = {
  experimental: {
    staleTimes: {
      dynamic: 30
    }
  },
  eslint: {
    dirs: ["app", "components", "lib", "hooks"]
  }
};

export default nextConfig;
