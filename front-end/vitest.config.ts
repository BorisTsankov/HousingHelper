import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react-swc'
import path from 'node:path'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/tests/setup.ts'],
    css: true,
    include: ['src/**/*.{test,spec}.{ts,tsx}'],

    reporters: [
      'default',
      ['junit', { outputFile: 'junit/junit.xml' }],
    ],

    coverage: {
      provider: 'v8',
      reporter: ['text', 'lcov', 'html'],
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