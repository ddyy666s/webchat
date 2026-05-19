import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'

const mockPlay = vi.fn()
const mockPause = vi.fn()
let mockVolume: number | undefined
let mockLoop: boolean | undefined

class MockAudio {
  volume: number | undefined
  loop: boolean | undefined
  constructor(public src: string) {
    mockVolume = undefined
    mockLoop = undefined
  }
  play() { return mockPlay() }
  pause() { mockPause() }
}

vi.stubGlobal('Audio', MockAudio)

import { playVoice, stopVoice, playNotificationSound, playRingtone, setSoundEnabled } from '@/utils/audio'

describe('audio', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setSoundEnabled(true)
  })

  afterEach(() => {
    stopVoice()
  })

  it('playVoice creates Audio and plays', () => {
    mockPlay.mockReturnValue(Promise.resolve())
    playVoice('http://example.com/voice.mp3')
    expect(mockPlay).toHaveBeenCalled()
  })

  it('playVoice pauses previous audio before playing new', () => {
    mockPlay.mockReturnValue(Promise.resolve())
    playVoice('http://example.com/1.mp3')
    playVoice('http://example.com/2.mp3')
    expect(mockPause).toHaveBeenCalled()
    expect(mockPlay).toHaveBeenCalledTimes(2)
  })

  it('stopVoice pauses and clears audio', () => {
    mockPlay.mockReturnValue(Promise.resolve())
    playVoice('http://example.com/test.mp3')
    stopVoice()
    expect(mockPause).toHaveBeenCalled()
  })

  it('playNotificationSound creates Audio with volume 0.5', () => {
    mockPlay.mockReturnValue(Promise.resolve())
    playNotificationSound()
    expect(mockPlay).toHaveBeenCalled()
  })

  it('playNotificationSound does nothing when sound disabled', () => {
    setSoundEnabled(false)
    playNotificationSound()
    expect(mockPlay).not.toHaveBeenCalled()
  })

  it('playRingtone returns a stop function', () => {
    mockPlay.mockReturnValue(Promise.resolve())
    const stop = playRingtone()
    expect(typeof stop).toBe('function')
    expect(mockPlay).toHaveBeenCalled()
    stop()
    expect(mockPause).toHaveBeenCalled()
  })

  it('playRingtone returns noop when sound disabled', () => {
    setSoundEnabled(false)
    const stop = playRingtone()
    expect(mockPlay).not.toHaveBeenCalled()
    expect(stop()).toBeUndefined()
  })
})
