import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react-swc'
import path from 'node:path'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      // optional: keep TS path aliases in sync
      '@': path.resolve(__dirname, 'src'),
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/tests/setup.ts'],
    css: true, // lets you import CSS in components during tests
    // optional: tighten which files are tests
    include: ['src/**/*.{test,spec}.{ts,tsx}'],
    coverage: {
      provider: 'v8',               // fast & built-in; Node 20+ recommended
      reporter: ['text', 'lcov'],   // add 'html' if you want a browsable report
      reportsDirectory: './coverage',
      include: ['src/**/*.{ts,tsx}'],
      exclude: [
        'src/**/__mocks__/**',
        'src/**/types/**',
        'src/main.tsx',
        '**/*.d.ts',
      ],
    },
  },
})
