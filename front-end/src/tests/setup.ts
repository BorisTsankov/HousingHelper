import '@testing-library/jest-dom'
import 'whatwg-fetch'

import { afterEach, vi } from 'vitest'
import { cleanup } from '@testing-library/react'
afterEach(() => cleanup())

if (!('matchMedia' in window)) {

  window.matchMedia = (query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: () => {},
    removeListener: () => {},
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => false,
  })
}

import { TextEncoder, TextDecoder } from 'util'
if (!globalThis.TextEncoder) globalThis.TextEncoder = TextEncoder as any
if (!globalThis.TextDecoder) globalThis.TextDecoder = TextDecoder as any

window.scrollTo = vi.fn()