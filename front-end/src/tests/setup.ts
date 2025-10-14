// Extends jest-style DOM matchers: toBeInTheDocument, toHaveTextContent, etc.
import '@testing-library/jest-dom'
import 'whatwg-fetch'

// Clean up automatically between tests
import { afterEach, vi } from 'vitest'
import { cleanup } from '@testing-library/react'
afterEach(() => cleanup())

// jsdom shims for common React/web APIs your code might use
// matchMedia (used by MUI, Tailwind dark-mode queries, etc.)
if (!('matchMedia' in window)) {
  // @ts-expect-error - define shim
  window.matchMedia = (query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: () => {},           // deprecated
    removeListener: () => {},        // deprecated
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => false,
  })
}

// TextEncoder/Decoder (node < 22 sometimes misses these in tests)
import { TextEncoder, TextDecoder } from 'util'
if (!globalThis.TextEncoder) globalThis.TextEncoder = TextEncoder as any
if (!globalThis.TextDecoder) globalThis.TextDecoder = TextDecoder as any

// Optional: mock scrollTo to avoid "not implemented" noise
window.scrollTo = vi.fn()

// Optional: fetch polyfill (if you call fetch in components/hooks)
import 'whatwg-fetch'
