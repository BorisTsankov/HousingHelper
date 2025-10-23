import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { PropertyCard } from '../components/gallery/PropertyCard' // adjust path if needed

describe('PropertyCard', () => {
  const mockProperty = {
    image: 'https://example.com/image.jpg',
    title: 'Cozy Apartment',
    location: 'Eindhoven, Netherlands',
    price: '€1200 / month',
  }

  it('renders property details correctly', () => {
    render(<PropertyCard item={mockProperty} />)

    expect(
      screen.getByRole('heading', { name: /cozy apartment/i })
    ).toBeInTheDocument()

    expect(screen.getByText(/eindhoven, netherlands/i)).toBeInTheDocument()

    expect(screen.getByText(/€1200 \/ month/i)).toBeInTheDocument()

    const img = screen.getByRole('img', { name: /cozy apartment/i })
    expect(img).toHaveAttribute('src', mockProperty.image)
    expect(img).toHaveAttribute('alt', mockProperty.title)
  })
})
